'use strict';
const app = document.querySelector('#app');
const esc = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const money = value => new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(value);
async function api(path, options = {}) {
  const csrf = options.method && options.method !== 'GET' ? await fetch('/api/auth/csrf').then(r=>r.json()) : null;
  if(csrf) options.headers={...options.headers,[csrf.headerName]:csrf.token};
  const response = await fetch('/api/' + path, { ...options, headers: {'Content-Type':'application/json', ...options.headers} });
  if (response.status === 204) return null;
  const data = await response.json().catch(()=>({}));
  if (!response.ok) throw Object.assign(new Error([data.message, ...Object.entries(data.errors || {}).map(([k,v]) => `${k}: ${v}`)].filter(Boolean).join(' · ') || `Lỗi HTTP ${response.status}`), {status:response.status});
  return data;
}
const schemas = {
 'sach': {title:'Quản lý sách', single:'sách', columns:['maSach','tenSach','tacGia','giaBan','soLuongTon'], fields:[['maSach','Mã sách','text',true,30],['tenSach','Tên sách','text',true,255],['tacGia','Tác giả','text',true,150],['isbn','ISBN','text',false,20],['namXuatBan','Năm xuất bản','number'],['giaBan','Giá bán','number',true],['soLuongTon','Số lượng tồn','number',true],['danhMucId','Danh mục','select',true],['nhaXuatBanId','Nhà xuất bản','select',true],['hinhAnh','Đường dẫn hình ảnh','text',false,500],['moTa','Mô tả','textarea'],['trangThai','Trạng thái','boolean',true]]},
 'danh-muc': {title:'Quản lý danh mục',single:'danh mục',columns:['tenDanhMuc','moTa','trangThai'],fields:[['tenDanhMuc','Tên danh mục','text',true,100],['moTa','Mô tả','textarea',false,1000],['trangThai','Trạng thái','boolean',true]]},
 'nha-xuat-ban': {title:'Quản lý nhà xuất bản',single:'nhà xuất bản',columns:['tenNhaXuatBan','soDienThoai','email','trangThai'],fields:[['tenNhaXuatBan','Tên nhà xuất bản','text',true,150],['diaChi','Địa chỉ','text',false,500],['soDienThoai','Số điện thoại','tel',false,20],['email','Email','email',false,150],['trangThai','Trạng thái','boolean',true]]},
 'khach-hang': {title:'Quản lý khách hàng',single:'khách hàng',columns:['hoTen','soDienThoai','email','diaChi'],fields:[['hoTen','Họ tên','text',true,150],['soDienThoai','Số điện thoại','tel',false,20],['email','Email','email',false,150],['diaChi','Địa chỉ','text',false,500]]}
};
function message(text, error=false, target=document.querySelector('#message')) { if(target) {target.textContent=text;target.className='notice'+(error?' error':'');target.hidden=!text;} }
async function allPages(resource) {
 const first = await api(`${resource}?page=0&size=100`);
 const rows = [...first.content];
 for(let p=1;p<first.totalPages;p++) rows.push(...(await api(`${resource}?page=${p}&size=100`)).content);
 return rows;
}
async function manager(resource, admin=false) {
 const config=schemas[resource]; let page=0, data, busy=false;
 app.innerHTML=`<span class="eyebrow">${admin?'Không gian quản trị':'Dữ liệu nhà sách'}</span><h1>${config.title}</h1><p>Thêm, cập nhật và tra cứu dữ liệu trực tiếp từ REST API.</p>${admin?'<div class="tabs"><a class="button secondary" href="/quan-tri.html?module=sach">Sách</a><a class="button secondary" href="/quan-tri.html?module=danh-muc">Danh mục</a><a class="button secondary" href="/quan-tri.html?module=nha-xuat-ban">Nhà xuất bản</a><a class="button secondary" href="/quan-tri.html?module=khach-hang">Khách hàng</a></div>':''}<div class="toolbar"><button id="add">+ Thêm ${config.single}</button><button id="refresh" class="secondary">Tải lại dữ liệu</button></div><div id="message" role="alert" hidden></div><div id="rows" class="table-wrap" aria-live="polite"></div><div class="pager"><button id="prev" class="secondary">← Trước</button><span id="page"></span><button id="next" class="secondary">Sau →</button></div><dialog aria-labelledby="dialog-title"><h2 id="dialog-title"></h2><form id="edit-form"><div id="fields" class="form-grid"></div><div id="form-error" role="alert" hidden></div><div class="actions"><button id="save" type="submit">Lưu dữ liệu</button><button id="cancel" type="button" class="secondary">Hủy</button></div></form></dialog>`;
 const dialog=document.querySelector('dialog'), form=document.querySelector('#edit-form');
 async function load(){
  document.querySelector('#rows').textContent='Đang tải dữ liệu…';
  try {
   data=await api(`${resource}?page=${page}&size=10`);
   if(page>0 && !data.content.length){page--;return await load();}
   const label=k=>config.fields.find(f=>f[0]===k)[1];
   document.querySelector('#rows').innerHTML=`<table><thead><tr>${config.columns.map(k=>`<th scope="col">${label(k)}</th>`).join('')}<th scope="col">Thao tác</th></tr></thead><tbody>${data.content.map(row=>`<tr>${config.columns.map(k=>`<td>${k==='giaBan'?money(row[k]):k==='trangThai'?(row[k]?'Hoạt động':'Tạm ngừng'):esc(row[k])}</td>`).join('')}<td><button data-edit="${row.id}" class="secondary">Sửa</button><button data-delete="${row.id}" class="danger">Xóa</button></td></tr>`).join('') || `<tr><td colspan="${config.columns.length+1}">Chưa có dữ liệu. Hãy thêm bản ghi đầu tiên.</td></tr>`}</tbody></table>`;
   document.querySelector('#page').textContent=`Trang ${data.totalPages?page+1:0}/${data.totalPages} · ${data.totalElements} bản ghi`;
   document.querySelector('#prev').disabled=page===0;document.querySelector('#next').disabled=page+1>=data.totalPages;
  }catch(e){document.querySelector('#rows').textContent='Không tải được dữ liệu. Bấm Tải lại dữ liệu để thử lại.';message(e.message,true);}
 }
 async function edit(id){
  try {
   const row=id?await api(resource+'/'+id):{};
   const [categories,publishers]=resource==='sach'?await Promise.all([allPages('danh-muc'),allPages('nha-xuat-ban')]):[[],[]];
   document.querySelector('#dialog-title').textContent=(id?'Cập nhật ':'Thêm ')+config.single;
   document.querySelector('#fields').innerHTML=config.fields.map(([key,label,type,required,max])=>{
    let input;
    if(type==='select') {const list=key==='danhMucId'?categories:publishers;input=`<select name="${key}" required><option value="">Chọn ${label.toLowerCase()}</option>${list.map(v=>`<option value="${v.id}" ${row[key]===v.id?'selected':''}>${esc(v.tenDanhMuc??v.tenNhaXuatBan)}${v.trangThai?'':' (tạm ngừng)'}</option>`).join('')}</select>`;}
    else if(type==='boolean') input=`<select name="${key}"><option value="true">Hoạt động</option><option value="false" ${row[key]===false?'selected':''}>Tạm ngừng</option></select>`;
    else if(type==='textarea') input=`<textarea name="${key}" ${max?`maxlength="${max}"`:''}>${esc(row[key])}</textarea>`;
    else input=`<input name="${key}" type="${type}" value="${esc(row[key])}" ${required?'required':''} ${max?`maxlength="${max}"`:''} ${type==='number'?`min="${key==='namXuatBan'?1000:0}" ${key==='namXuatBan'?'max="9999"':''} step="${key==='giaBan'?'0.01':1}"`:''} ${type==='tel'?'pattern="[0-9+() .-]{8,20}"':''}>`;
    return `<label><span>${label}${required?' *':''}</span>${input}</label>`;
   }).join('');
   if(resource==='sach'){
    const field=document.querySelector('[name="hinhAnh"]');
    field.insertAdjacentHTML('afterend','<input id="book-image-file" type="file" accept="image/png,image/jpeg" aria-label="Tải ảnh sách"><img id="book-image-preview" class="upload-preview" alt="Xem trước ảnh sách" hidden>');
    const preview=document.querySelector('#book-image-preview');
    const refresh=()=>{const url=field.value.trim();preview.hidden=!/^(\/images\/|https?:\/\/)/.test(url);if(!preview.hidden)preview.src=url;};field.oninput=refresh;refresh();
    document.querySelector('#book-image-file').onchange=async e=>{if(!e.target.files[0])return;document.querySelector('#save').disabled=true;busy=true;try{field.value=await uploadImage(e.target.files[0],'sach');refresh();message('Ảnh đã tải lên. Bấm Lưu dữ liệu để cập nhật sách.',false,document.querySelector('#form-error'));}catch(err){message(err.message,true,document.querySelector('#form-error'));}finally{busy=false;document.querySelector('#save').disabled=false;}};
   }
   message('',false,document.querySelector('#form-error'));dialog.showModal();
   form.onsubmit=async event=>{
    event.preventDefault();if(busy)return;busy=true;document.querySelector('#save').disabled=true;
    try{
     const values=Object.fromEntries(new FormData(form));
     config.fields.forEach(([k,,type])=>{if(type==='number'||type==='select')values[k]=values[k]===''?null:Number(values[k]);else if(type==='boolean')values[k]=values[k]==='true';else values[k]=values[k].trim();});
     await api(resource+(id?'/'+id:''),{method:id?'PUT':'POST',body:JSON.stringify(values)});
     dialog.close();message('Đã lưu dữ liệu thành công.');await load();
    }catch(e){message(e.message,true,document.querySelector('#form-error'));}
    finally{busy=false;document.querySelector('#save').disabled=false;}
   };
  }catch(e){message(e.message,true);}
 }
 document.querySelector('#add').onclick=()=>edit();
 document.querySelector('#cancel').onclick=()=>{if(!busy)dialog.close();};
 dialog.addEventListener('cancel',e=>{if(busy)e.preventDefault();});
 document.querySelector('#refresh').onclick=()=>{message('');load();};
 document.querySelector('#prev').onclick=()=>{if(page>0){page--;load();}};
 document.querySelector('#next').onclick=()=>{if(data&&page+1<data.totalPages){page++;load();}};
 document.querySelector('#rows').onclick=async event=>{
  const button=event.target.closest('button');if(!button)return;
  if(button.dataset.edit)return edit(button.dataset.edit);
  if(button.dataset.delete&&confirm(`Xóa ${config.single} này? Thao tác sẽ cập nhật cơ sở dữ liệu.`)){
   button.disabled=true;
   try{await api(resource+'/'+button.dataset.delete,{method:'DELETE'});message('Đã xóa dữ liệu.');await load();}catch(e){message(e.message,true);button.disabled=false;}
  }
 };
 await load();
}

let currentUser={role:'GUEST'};
let favoriteIds=new Set();
function cover(book){
 const url=book.hinhAnh;
 return url && /^(\/images\/|https?:\/\/)/.test(url)?esc(url):'/images/book-cover.svg';
}
function bookArt(b,large=false){
 if(b.hinhAnh)return `<img class="${large?'feature-image':'cover'}" src="${cover(b)}" alt="Bìa ${esc(b.tenSach)}">`;
 const tones=['forest','clay','navy','gold','plum','sage'];
 return `<div class="book-art ${tones[b.id%tones.length]} ${large?'large':''}" role="img" aria-label="Bìa minh họa ${esc(b.tenSach)}"><small>TỦ SÁCH NHÓM 14</small><strong>${esc(b.tenSach)}</strong><span class="book-mark">✦</span><em>${esc(b.tacGia)}</em></div>`;
}
function card(b){return `<article class="shop-card"><a class="cover-link" href="/chi-tiet.html?id=${b.id}">${bookArt(b)}</a><div class="book-meta"><small>${esc(b.maSach)} · ${b.soLuongTon>0?'Có sẵn':'Tạm hết hàng'}</small><h3><a href="/chi-tiet.html?id=${b.id}">${esc(b.tenSach)}</a></h3><p>${esc(b.tacGia)}</p><div class="book-bottom"><strong>${money(b.giaBan)}</strong>${favoriteButton(b.id)}<a href="/chi-tiet.html?id=${b.id}" aria-label="Xem ${esc(b.tenSach)}">↗</a></div></div></article>`;}
async function catalog(home=false){
 document.body.classList.add('storefront');
 app.innerHTML='<p role="status">Đang sắp xếp tủ sách…</p>';
 try{
 const [books,categories]=await Promise.all([allPages('sach'),allPages('danh-muc')]);
 let selected=Number(new URLSearchParams(location.search).get('category'))||0, query=new URLSearchParams(location.search).get('q')||'',page=0;
 const available=books.filter(b=>b.trangThai);
 const feature=available.find(b=>b.danhMucId===2)||available[0];
 const chips=categories.filter(c=>c.trangThai).map((c,i)=>`<button class="category-chip" data-category="${c.id}"><span>${['▤','❧','◈','✦','☀','Aa','⚛','◎','▧','♡'][i%10]}</span>${esc(c.tenDanhMuc)}</button>`).join('');
 app.innerHTML=`<div class="shop-search"><span>Đọc một cuốn sách, mở một thế giới.</span><form id="search-books" role="search"><input name="q" aria-label="Tìm tên sách hoặc tác giả" placeholder="Bạn đang tìm cuốn sách nào?" value="${esc(query)}"><button>Tìm sách ↗</button></form></div>${home?`<section class="store-hero"><div class="hero-copy"><span class="eyebrow">GÓC NHỎ CHO NGƯỜI YÊU SÁCH</span><h1>Chậm lại một chút.<br><i>Đọc thêm một trang.</i></h1><p>Từ những câu chuyện chạm đến trái tim đến kiến thức mở ra góc nhìn mới. Cuốn sách tiếp theo của bạn đang ở đây.</p><a class="button" href="#book-shelf">Tìm cuốn sách của bạn <span>→</span></a><div class="hero-note"><span>✦</span> ${available.length} đầu sách · ${categories.length} thể loại để khám phá</div></div><div class="hero-display">${feature?`<a href="/chi-tiet.html?id=${feature.id}" class="hero-book">${bookArt(feature,true)}</a><div class="featured-caption"><span>GỢI Ý TRÊN KỆ</span><strong>${esc(feature.tenSach)}</strong><a href="/chi-tiet.html?id=${feature.id}">Mở sách và đọc thử →</a></div>`:''}<span class="hero-orbit">MỘT CUỐN SÁCH<br>VẠN ĐIỀU HAY</span></div></section><div class="reading-strip"><span>▤ <strong>Chọn sách theo sở thích</strong><small>10 chủ đề cho nhiều góc nhìn</small></span><span>☷ <strong>Đọc thử trước khi chọn</strong><small>Khám phá nội dung mỗi cuốn sách</small></span><span>♡ <strong>Nuôi dưỡng thói quen đọc</strong><small>Mỗi ngày dành một chút cho bản thân</small></span></div>`:''}${currentUser.role!=='GUEST'?'<section id="favorite-summary" class="favorite-summary" aria-live="polite"></section>':''}<section class="category-section"><div class="section-heading"><div><span class="eyebrow">BẮT ĐẦU TỪ ĐIỀU BẠN THÍCH</span><h2>Hôm nay, bạn muốn đọc gì?</h2></div><button id="all-categories" class="text-button">Tất cả thể loại →</button></div><div class="category-list">${chips}</div></section><section id="book-shelf"><div class="section-heading"><div><span class="eyebrow">NHỮNG TRANG SÁCH ĐANG CHỜ BẠN</span><h2 id="shelf-title">Khám phá trên kệ</h2></div><label class="sort-label">Sắp xếp<select id="sort-books"><option value="default">Theo mã sách</option><option value="price-asc">Giá tăng dần</option><option value="price-desc">Giá giảm dần</option><option value="title">Tên sách A–Z</option></select></label></div><div id="result-summary" aria-live="polite"></div><div class="shop-grid" id="books"></div><div class="pager"><button id="prev" class="secondary">← Trước</button><span id="page"></span><button id="next" class="secondary">Sau →</button></div></section>${home?'<section class="editorial"><span class="eyebrow">DÀNH MỘT CHÚT THỜI GIAN CHO MÌNH</span><h2>Không cần đọc thật nhanh.<br>Chỉ cần bắt đầu.</h2><p>Một câu chuyện trước giờ ngủ, một ý tưởng cho ngày mới, hay một chủ đề bạn luôn tò mò. Hãy chọn cuốn sách khiến bạn muốn lật trang tiếp theo.</p><a class="button" href="/sach.html?category=4">Khám phá sách kỹ năng sống →</a></section>':''}<div class="store-end"><div><strong>NHÀ SÁCH NHÓM 14</strong><p>Đồng hành cùng những trang sách của bạn.</p></div><div><a href="/sach.html">Khám phá tủ sách</a><a href="/dang-nhap.html">Tài khoản bạn đọc</a></div></div>`;
 const normalize=v=>v.normalize('NFD').replace(/[\u0300-\u036f]/g,'').replace(/đ/g,'d').toLowerCase();
 function load(){let rows=available.filter(b=>(!selected||b.danhMucId===selected)&&normalize(b.tenSach+' '+b.tacGia).includes(normalize(query)));
 const sort=document.querySelector('#sort-books').value;if(sort==='price-asc')rows.sort((a,b)=>a.giaBan-b.giaBan);if(sort==='price-desc')rows.sort((a,b)=>b.giaBan-a.giaBan);if(sort==='title')rows.sort((a,b)=>a.tenSach.localeCompare(b.tenSach,'vi'));
 const pages=Math.ceil(rows.length/8);page=Math.max(0,Math.min(page,pages-1));
 document.querySelector('#books').innerHTML=rows.slice(page*8,page*8+8).map(card).join('')||'<div class="empty-shelf"><h3>Chưa tìm thấy cuốn sách phù hợp</h3><p>Thử một tên sách, tác giả hoặc thể loại khác nhé.</p></div>';
 document.querySelector('#shelf-title').textContent=selected?(categories.find(c=>c.id===selected)?.tenDanhMuc||'Khám phá trên kệ'):'Khám phá trên kệ';
 document.querySelector('#result-summary').textContent=`${rows.length} cuốn sách${query?' cho “'+query+'”':''}`;
 document.querySelector('#page').textContent=`${pages?page+1:0} / ${pages}`;document.querySelector('#prev').disabled=page===0;document.querySelector('#next').disabled=page+1>=pages;
 document.querySelectorAll('[data-category]').forEach(el=>el.classList.toggle('selected',Number(el.dataset.category)===selected));
 }
 document.querySelector('#search-books').onsubmit=e=>{e.preventDefault();query=new FormData(e.currentTarget).get('q').trim();page=0;load();document.querySelector('#book-shelf').scrollIntoView({behavior:'smooth'});};
 document.querySelectorAll('[data-category]').forEach(el=>el.onclick=()=>{selected=Number(el.dataset.category);page=0;load();document.querySelector('#book-shelf').scrollIntoView({behavior:'smooth'});});
 document.querySelector('#all-categories').onclick=()=>{selected=0;query='';document.querySelector('[name=q]').value='';page=0;load();};
 document.querySelector('#sort-books').onchange=()=>{page=0;load();};document.querySelector('#prev').onclick=()=>{page--;load();};document.querySelector('#next').onclick=()=>{page++;load();};load();await renderFavoriteSummary();
 }catch(e){app.innerHTML=`<section class="panel"><h1>Tủ sách chưa tải được</h1><p role="alert">${esc(e.message)}</p><button onclick="location.reload()">Thử lại</button></section>`;}
}
async function detail(){
 app.innerHTML='<p>Đang tải sách…</p>';
 try{const id=new URLSearchParams(location.search).get('id');if(!/^\d+$/.test(id||''))throw Error('Mã sách không hợp lệ');
 const b=await api('sach/'+id), c=await api('danh-muc/'+b.danhMucId),n=await api('nha-xuat-ban/'+b.nhaXuatBanId);
 app.innerHTML=`<a href="/sach.html">← Quay lại tủ sách</a><section class="detail panel"><img class="detail-cover" src="${cover(b)}" alt="Bìa ${esc(b.tenSach)}"><div><span class="eyebrow">${esc(c.tenDanhMuc)}</span><h1>${esc(b.tenSach)}</h1><p>Tác giả: <strong>${esc(b.tacGia)}</strong></p><p>Nhà xuất bản: ${esc(n.tenNhaXuatBan)}</p><p>Năm xuất bản: ${esc(b.namXuatBan||'Đang cập nhật')}</p><div class="price">${money(b.giaBan)}</div><p>${b.soLuongTon>0?'Còn '+b.soLuongTon+' cuốn':'Tạm hết hàng'}</p><a class="button" href="#preview">Đọc thử</a> ${favoriteButton(b.id)}</div></section><section class="panel"><h2>Giới thiệu sản phẩm</h2><p>${esc(b.moTa||'Một đầu sách thuộc thể loại '+c.tenDanhMuc+'. Khám phá nội dung minh họa ở phần đọc thử bên dưới.')}</p></section><section class="panel" id="preview"><h2>Đọc thử</h2><p class="notice">Đoạn minh họa do nhóm biên soạn cho bài thực hành, không phải trích đoạn từ sách xuất bản.</p><h3>Mở đầu hành trình</h3><p>Mỗi hành trình khám phá đều bắt đầu bằng một câu hỏi. Với chủ đề ${esc(c.tenDanhMuc.toLowerCase())}, chúng ta thử dừng lại, quan sát những điều quen thuộc và tìm một góc nhìn mới.</p><p>Hãy dành vài phút ghi lại điều bạn muốn tìm hiểu. Chia mục tiêu thành những bước nhỏ, đọc từng phần và liên hệ với trải nghiệm của mình. Kiến thức trở nên hữu ích khi bạn có thể kể lại bằng lời của chính mình.</p><p>Trước khi sang trang tiếp theo, hãy tự hỏi: hôm nay mình có thể thử áp dụng điều gì? Một thay đổi nhỏ cũng có thể mở ra những khám phá thú vị.</p></section>`;
 }catch(e){app.innerHTML=`<p role="alert">${esc(e.message)}</p><a href="/sach.html">Về tủ sách</a>`;}
}
function login(admin=false,register=false){
 app.innerHTML=`<section class="panel login"><span class="eyebrow">${admin?'Khu vực quản trị':'Nhà sách Nhóm 14'}</span><h1>${register?'Tạo tài khoản':admin?'Đăng nhập admin':'Đăng nhập bạn đọc'}</h1><form id="login-form">${register?'<label>Họ tên<input name="hoTen" required maxlength="150"></label><label>Email<input name="email" type="email" required maxlength="150"></label>':''}<label>Tên đăng nhập<input name="username" required autocomplete="username" pattern="[a-zA-Z0-9_]{4,50}"></label><label>Mật khẩu<input name="password" type="password" required minlength="8" maxlength="72" autocomplete="${register?'new-password':'current-password'}"></label><button type="submit">${register?'Đăng ký':'Đăng nhập'}</button></form><div id="message" role="alert" hidden></div><p>${register?'<a href="/dang-nhap.html">Đã có tài khoản? Đăng nhập</a>':admin?'<a href="/dang-nhap.html">Đăng nhập dành cho bạn đọc</a>':'<a href="/dang-ky.html">Tạo tài khoản bạn đọc</a> · <a href="/admin-login.html">Dành cho admin</a>'}</p></section>`;
 document.querySelector('#login-form').onsubmit=async e=>{e.preventDefault();const button=e.currentTarget.querySelector('button');button.disabled=true;try{const values=Object.fromEntries(new FormData(e.currentTarget));values.username=values.username.trim();if(register){await api('auth/register',{method:'POST',body:JSON.stringify(values)});location.href='/dang-nhap.html?registered=1';return;}
 const csrf=await api('auth/csrf');const res=await fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded',[csrf.headerName]:csrf.token},body:new URLSearchParams(values)});const data=await res.json().catch(()=>({}));if(res.status===404)throw Error('Phiên ứng dụng này chưa có chức năng đăng nhập. Hãy mở bản mới hoặc khởi động lại ứng dụng.');if(res.status===403)throw Error('Phiên đăng nhập đã hết hạn. Hãy tải lại trang rồi đăng nhập lại.');if(!res.ok)throw Error(data.message||'Đăng nhập thất bại');
 if(admin&&data.role!=='ADMIN'){await api('auth/logout',{method:'POST'});throw Error('Tài khoản này không có quyền admin');}
 location.href=data.role==='ADMIN'?'/quan-tri.html':'/sach.html';
 }catch(err){message(err.status===404?'Phiên ứng dụng này chưa có chức năng đăng nhập. Hãy mở bản mới hoặc khởi động lại ứng dụng.':err.message,true);}finally{button.disabled=false;}};
 if(new URLSearchParams(location.search).has('registered'))message('Đăng ký thành công. Hãy đăng nhập.');
}
function favoriteButton(id){const saved=favoriteIds.has(id);return `<button class="favorite-button ${saved?'saved':''}" data-favorite="${id}" aria-pressed="${saved}" aria-label="${saved?'Bỏ khỏi':'Thêm vào'} sách yêu thích">${saved?'♥':'♡'}</button>`;}
const emptyFavorites='<section class="empty-shelf"><h2>Chưa có sách yêu thích</h2><p>Nhấn ♡ trên cuốn sách bạn thích để lưu vào đây.</p><a class="button" href="/sach.html">Khám phá tủ sách →</a></section>';
function showFavoriteError(text){let el=document.querySelector('#favorite-feedback');if(!el){el=document.createElement('div');el.id='favorite-feedback';el.setAttribute('role','alert');document.body.append(el);}el.textContent=text;el.hidden=!text;}
async function renderFavoriteSummary(){
 const target=document.querySelector('#favorite-summary');if(!target)return;
 try{const data=await api('yeu-thich/the-loai');
 if(!data.tongSoSach){target.innerHTML='<span class="eyebrow">GÓC ĐỌC CỦA BẠN</span><h2>Bạn thích đọc gì?</h2><p>Lưu sách bằng nút ♡. Thể loại yêu thích sẽ được tính từ chính những cuốn sách bạn lưu.</p>';return;}
 const max=data.theLoai[0].soSach,leaders=data.theLoai.filter(c=>c.soSach===max);
 target.innerHTML=`<div class="section-heading"><div><span class="eyebrow">GÓC ĐỌC CỦA ${esc(currentUser.username)}</span><h2>Thể loại yêu thích của bạn</h2></div><a href="/yeu-thich.html">${data.tongSoSach} sách đã lưu →</a></div><p>Bạn lưu nhiều sách nhất ở ${leaders.map(c=>esc(c.tenDanhMuc)).join(', ')} — ${max} cuốn${leaders.length>1?' ở mỗi thể loại':' đã lưu'}.</p><div class="favorite-genres">${data.theLoai.map(c=>`<a href="/sach.html?category=${c.id}"><span>${esc(c.tenDanhMuc)}</span><strong>${c.soSach} cuốn</strong><span class="genre-meter"><i style="width:${c.soSach/data.tongSoSach*100}%"></i></span></a>`).join('')}</div><small>Tính từ số sách đang lưu theo từng thể loại. Bỏ lưu sách sẽ cập nhật lại kết quả.</small>`;
 }catch(e){target.hidden=e.status===404;if(e.status!==404)target.innerHTML='<p>Thống kê sẽ hiển thị khi kết nối lại được.</p>';}
}
async function favoritesPage(){
 document.body.classList.add('storefront');
 app.innerHTML='<h1>Sách yêu thích</h1><p>Những cuốn sách bạn muốn dành thời gian khám phá.</p><section id="favorite-summary" class="favorite-summary" aria-live="polite"></section><div id="favorite-books" class="shop-grid" aria-live="polite"></div>';
 showFavoriteError('');
 try{const rows=await allPages('yeu-thich');document.querySelector('#favorite-books').innerHTML=rows.map(card).join('')||emptyFavorites;
 if(rows.length)await renderFavoriteSummary();else document.querySelector('#favorite-summary').hidden=true;
 }catch(e){if(e.status===401){location.href='/dang-nhap.html';return;}
 document.querySelector('#favorite-summary').hidden=true;
 document.querySelector('#favorite-books').innerHTML=e.status===404?emptyFavorites:'<section class="empty-shelf"><h2>Tạm thời chưa tải được danh sách</h2><p>Bạn hãy tải lại trang để thử lại.</p><button onclick="location.reload()">Thử lại</button></section>';
 }

}
document.addEventListener('click',async e=>{
 const button=e.target.closest('[data-favorite]');if(!button)return;
 if(currentUser.role==='GUEST'){location.href='/dang-nhap.html';return;}
 const id=Number(button.dataset.favorite),saved=favoriteIds.has(id);button.disabled=true;showFavoriteError('');
 try{await api('yeu-thich/'+id,{method:saved?'DELETE':'PUT'});if(saved)favoriteIds.delete(id);else favoriteIds.add(id);
 document.querySelectorAll(`[data-favorite="${id}"]`).forEach(el=>{el.outerHTML=favoriteButton(id);});
 if(location.pathname==='/yeu-thich.html')await favoritesPage();else await renderFavoriteSummary();
 }catch(err){showFavoriteError('Chưa lưu được thay đổi: '+err.message);button.disabled=false;}
});
async function uploadImage(file,type){
 if(file.size>5*1024*1024)throw Error('Ảnh tối đa 5 MB');
 const csrf=await api('auth/csrf'),body=new FormData();body.append('file',file);
 const res=await fetch('/api/anh/'+type,{method:'POST',headers:{[csrf.headerName]:csrf.token},body});
 const data=await res.json().catch(()=>({}));if(!res.ok)throw Error(data.message||'Không tải được ảnh. Hãy thử ảnh JPG/PNG dưới 5 MB.');return data.url;
}
async function profilePage(){
 app.innerHTML='<p>Đang tải thông tin cá nhân…</p>';
 try{
 const data=await api('ho-so');
 app.innerHTML=`<span class="eyebrow">KHÔNG GIAN CỦA BẠN</span><h1>Trang cá nhân</h1><div class="profile-layout"><aside class="panel profile-aside"><div id="avatar-placeholder" class="avatar-placeholder">${esc(data.tenDangNhap[0].toUpperCase())}</div><img id="avatar-preview" class="avatar" alt="Ảnh đại diện" hidden><h2>${esc(data.tenDangNhap)}</h2><label class="avatar-label">Đổi ảnh đại diện<input id="avatar-file" type="file" accept="image/png,image/jpeg"></label><p>JPG hoặc PNG, tối đa 5 MB.</p><a href="/yeu-thich.html">♡ Sách yêu thích</a></aside><section class="panel"><h2>Thông tin cá nhân</h2><p>Bạn có thể cập nhật thông tin bất cứ lúc nào.</p><form id="profile-form"><div class="form-grid"><label>Tên đăng nhập<input value="${esc(data.tenDangNhap)}" readonly></label><label>Họ tên *<input name="hoTen" value="${esc(data.hoTen)}" required maxlength="150" autocomplete="name"></label><label>Email *<input name="email" value="${esc(data.email)}" type="email" required maxlength="150" autocomplete="email"></label><label>Số điện thoại<input name="soDienThoai" value="${esc(data.soDienThoai)}" type="tel" pattern="[0-9+() .-]{8,20}" maxlength="20" autocomplete="tel"></label></div><label>Địa chỉ<input name="diaChi" value="${esc(data.diaChi)}" maxlength="500" autocomplete="street-address"></label><label>Thể loại yêu thích của bạn<textarea name="theLoaiYeuThich" maxlength="500" placeholder="Ví dụ: Văn học, Trinh thám, Khoa học…">${esc(data.theLoaiYeuThich)}</textarea></label><p class="field-hint">Tự nhập thể loại bạn thích. Phần này độc lập với thống kê sách đã lưu.</p><input name="anhDaiDien" type="hidden" value="${esc(data.anhDaiDien)}"><div id="message" role="status" hidden></div><button id="profile-save" type="submit">Lưu thông tin</button></form></section></div><section id="favorite-summary" class="favorite-summary"></section>`;
 const form=document.querySelector('#profile-form'),save=document.querySelector('#profile-save');
 function preview(){const url=form.elements.anhDaiDien.value;document.querySelector('#avatar-preview').hidden=!url;document.querySelector('#avatar-placeholder').hidden=!!url;if(url)document.querySelector('#avatar-preview').src=url;}
 preview();
 document.querySelector('#avatar-file').onchange=async e=>{if(!e.target.files[0])return;save.disabled=true;try{form.elements.anhDaiDien.value=await uploadImage(e.target.files[0],'dai-dien');preview();message('Ảnh đã tải lên. Bấm Lưu thông tin để lưu vào hồ sơ.');}catch(err){message(err.message,true);}finally{save.disabled=false;}};
 form.onsubmit=async e=>{e.preventDefault();save.disabled=true;try{await api('ho-so',{method:'PUT',body:JSON.stringify(Object.fromEntries(new FormData(form)))});message('Đã cập nhật thông tin cá nhân.');}catch(err){message(err.message,true);}finally{save.disabled=false;}};
 await renderFavoriteSummary();
 }catch(e){app.innerHTML='<section class="panel"><h1>Chưa tải được hồ sơ</h1><p>Hãy đăng nhập lại hoặc tải lại trang để thử lại.</p><a href="/dang-nhap.html">Đăng nhập</a></section>';}
}
async function start(){
 const path=location.pathname;try{currentUser=await api('auth/me');}catch(e){}
 if(currentUser.role!=='GUEST'){try{favoriteIds=new Set(await api('yeu-thich/ids'));}catch(e){favoriteIds=new Set();}}
 document.querySelector('nav').innerHTML=`<a href="/">Trang chủ</a><a href="/sach.html">Tủ sách</a><a href="/yeu-thich.html">♡ Yêu thích</a>${currentUser.role==='ADMIN'?'<a href="/quan-tri.html">Quản trị</a>':''}${currentUser.role==='GUEST'?'<a href="/dang-nhap.html">Đăng nhập</a>':`<a href="/ca-nhan.html">${esc(currentUser.username)} · Cá nhân</a><button id="logout" class="secondary">Đăng xuất</button>`}`;
 document.querySelector('#logout')?.addEventListener('click',async()=>{await api('auth/logout',{method:'POST'});location.href='/';});
 if(path==='/dang-nhap.html'||path==='/admin-login.html'||path==='/dang-ky.html')return login(path==='/admin-login.html',path==='/dang-ky.html');
 if(path==='/ca-nhan.html')return profilePage();
 if(path==='/yeu-thich.html')return favoritesPage();
 if(path==='/chi-tiet.html')return detail();
 if(path==='/quan-tri.html'||path==='/khach-hang.html'){
 if(currentUser.role!=='ADMIN'){location.href='/admin-login.html';return;}
 document.body.classList.add('admin-layout');
 const aside=document.createElement('aside');aside.className='admin-sidebar';aside.innerHTML='<h2>NHÀ SÁCH ADMIN</h2><p>Không gian quản trị</p><a href="/quan-tri.html">Tổng quan quản lý</a><a href="/quan-tri.html?module=sach">▤ Sách</a><a href="/quan-tri.html?module=danh-muc">☷ Danh mục</a><a href="/quan-tri.html?module=nha-xuat-ban">▣ Nhà xuất bản</a><a href="/quan-tri.html?module=khach-hang">♙ Khách hàng</a><a href="/">← Xem website</a>';document.body.prepend(aside);
 const module=new URLSearchParams(location.search).get('module')||'sach';return manager(path==='/khach-hang.html'?'khach-hang':Object.hasOwn(schemas,module)?module:'sach',true);}
 return catalog(path==='/'||path==='/index.html');
}
start();
