package vn.nhom14.qlns.controller;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.context.annotation.Configuration;

@RestController
@RequestMapping("/api/anh")
public class AnhController {
 private final Path directory;
 public AnhController(@Value("${app.upload-dir:uploads}") String folder){directory=Path.of(folder).toAbsolutePath().normalize();}
 @PostMapping({"/dai-dien","/sach"}) public Map<String,String> upload(@RequestParam("file") MultipartFile file) throws IOException {
  if(file.isEmpty()||file.getSize()>5*1024*1024)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chọn ảnh JPG hoặc PNG, tối đa 5 MB");
  BufferedImage image;
  try(var in=ImageIO.createImageInputStream(file.getInputStream())){
   var readers=ImageIO.getImageReaders(in);if(!readers.hasNext())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tệp không phải ảnh JPG hoặc PNG hợp lệ");
   var reader=readers.next();try{reader.setInput(in);String format=reader.getFormatName();if(!format.equalsIgnoreCase("png")&&!format.equalsIgnoreCase("jpeg"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ hỗ trợ JPG và PNG");if((long)reader.getWidth(0)*reader.getHeight(0)>16000000)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ảnh tối đa 16 triệu điểm ảnh");image=reader.read(0);}finally{reader.dispose();}
  }
  Files.createDirectories(directory);String name=UUID.randomUUID()+".png";ImageIO.write(image,"png",directory.resolve(name).toFile());return Map.of("url","/images/uploads/"+name);
 }
 @Configuration public static class Images implements WebMvcConfigurer {
  private final String location;
  public Images(@Value("${app.upload-dir:uploads}") String path){location=Path.of(path).toAbsolutePath().normalize().toUri().toString()+"/";}
  @Override public void addResourceHandlers(ResourceHandlerRegistry registry){registry.addResourceHandler("/images/uploads/**").addResourceLocations(location);}
 }
}
