package vn.nhom14.qlns.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@RequestMapping("/api/anh")
public class AnhController {
    private static final long MAX_UPLOAD_STORAGE=500L*1024*1024;
    private final Path directory;
    public AnhController(@Value("${app.upload-dir:uploads}") String folder){directory=Path.of(folder).toAbsolutePath().normalize();}

    @PostMapping("/dai-dien")
    public Map<String,String> avatar(Authentication auth,@RequestParam("file") MultipartFile file)throws IOException{
        // One stable file per account prevents public registration from creating unbounded orphan files.
        return store(read(file),"avatar-"+digest(auth.getName())+".png");
    }

    @PostMapping("/sach")
    public Map<String,String> book(@RequestParam("file") MultipartFile file)throws IOException{
        return store(read(file),"book-"+UUID.randomUUID()+".png");
    }

    private BufferedImage read(MultipartFile file)throws IOException{
        if(file.isEmpty()||file.getSize()>5L*1024*1024)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chọn ảnh JPG hoặc PNG, tối đa 5 MB");
        try(var in=ImageIO.createImageInputStream(file.getInputStream())){
            if(in==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tệp ảnh không hợp lệ");
            var readers=ImageIO.getImageReaders(in);
            if(!readers.hasNext())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tệp không phải ảnh JPG hoặc PNG hợp lệ");
            var reader=readers.next();
            try{
                reader.setInput(in);String format=reader.getFormatName();
                if(!format.equalsIgnoreCase("png")&&!format.equalsIgnoreCase("jpeg"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ hỗ trợ JPG và PNG");
                long width=reader.getWidth(0),height=reader.getHeight(0);
                if(width<1||height<1||width*height>16_000_000L)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ảnh tối đa 16 triệu điểm ảnh");
                BufferedImage image=reader.read(0);
                if(image==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Không đọc được nội dung ảnh");
                return image;
            }finally{reader.dispose();}
        }
    }

    private synchronized Map<String,String> store(BufferedImage image,String name)throws IOException{
        Files.createDirectories(directory);
        Path temporary=Files.createTempFile(directory,"upload-",".png");
        try{
            if(!ImageIO.write(image,"png",temporary.toFile()))throw new IOException("Không có bộ mã hóa PNG");
            Path destination=directory.resolve(name);
            long newSize=Files.size(temporary),used;
            try(var files=Files.list(directory)){
                used=files.filter(Files::isRegularFile).filter(path->!path.equals(temporary)&&!path.equals(destination))
                        .mapToLong(path->{try{return Files.size(path);}catch(IOException ex){throw new java.io.UncheckedIOException(ex);}}).sum();
            }catch(java.io.UncheckedIOException ex){throw ex.getCause();}
            if(used+newSize>MAX_UPLOAD_STORAGE)
                throw new ResponseStatusException(HttpStatus.INSUFFICIENT_STORAGE,"Kho ảnh đã đạt giới hạn 500 MB. Quản trị viên cần dọn ảnh không dùng.");
            try{Files.move(temporary,destination,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException ex){Files.move(temporary,destination,StandardCopyOption.REPLACE_EXISTING);}
            return Map.of("url","/images/uploads/"+name);
        }finally{Files.deleteIfExists(temporary);}
    }

    private static String digest(String value){
        try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))).substring(0,24);}
        catch(NoSuchAlgorithmException impossible){throw new IllegalStateException(impossible);}
    }

    @Configuration
    public static class Images implements WebMvcConfigurer{
        private final String location;
        public Images(@Value("${app.upload-dir:uploads}") String path){location=Path.of(path).toAbsolutePath().normalize().toUri().toString()+"/";}
        @Override public void addResourceHandlers(ResourceHandlerRegistry registry){registry.addResourceHandler("/images/uploads/**").addResourceLocations(location);}
    }
}
