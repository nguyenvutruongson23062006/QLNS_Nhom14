'use strict';
const app = document.querySelector('#app');
const esc = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const money = value => new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(value);
const isStaffRole = role => role==='ADMIN'||role==='NHAN_VIEN';
function formatFriendlyValidation(data, status) {
  const fieldGuides = {
    soDienThoai: 'Số điện thoại chưa đúng định dạng (8-20 ký tự số), vui lòng kiểm tra và nhập lại.',
    soDienThoaiNhan: 'Số điện thoại người nhận chưa đúng định dạng (8-20 ký tự số), vui lòng kiểm tra và nhập lại.',
    hoTen: 'Vui lòng kiểm tra và nhập lại họ và tên theo yêu cầu.',
    tenNguoiNhan: 'Vui lòng kiểm tra và nhập lại họ tên người nhận theo yêu cầu.',
    email: 'Địa chỉ email chưa đúng định dạng, vui lòng kiểm tra và nhập lại.',
    diaChi: 'Vui lòng kiểm tra và nhập lại địa chỉ.',
    diaChiGiao: 'Vui lòng kiểm tra và nhập lại địa chỉ nhận hàng.',
    diaChiGiaoHang: 'Vui lòng kiểm tra và nhập lại địa chỉ nhận hàng.',
    matKhau: 'Vui lòng nhập lại mật khẩu từ 8 đến 72 ký tự theo yêu cầu.',
    password: 'Vui lòng nhập lại mật khẩu từ 8 đến 72 ký tự theo yêu cầu.',
    tenDangNhap: 'Vui lòng kiểm tra lại tên đăng nhập (4-50 ký tự gồm chữ, số hoặc gạch dưới).',
    username: 'Vui lòng kiểm tra lại tên đăng nhập (4-50 ký tự gồm chữ, số hoặc gạch dưới).'
  };
  if (data && data.errors && Object.keys(data.errors).length > 0) {
    const list = Object.entries(data.errors).map(([field, msg]) => {
      if (fieldGuides[field]) return fieldGuides[field];
      if (msg && !msg.toLowerCase().includes('must') && !msg.toLowerCase().includes('size')) return msg;
      return 'Vui lòng kiểm tra và nhập lại mục ' + field + '.';
    });
    return 'Thông báo: ' + list.join(' · ');
  }
  if (data && data.message) {
    let msg = data.message;
    if (msg === 'Dữ liệu không hợp lệ') msg = 'Vui lòng kiểm tra và nhập lại các thông tin theo đúng yêu cầu.';
    return 'Thông báo: ' + msg;
  }
  return 'Thông báo: Vui lòng kiểm tra và nhập lại theo yêu cầu.';
}

async function api(path, options = {}) {
  const csrf = options.method && options.method !== 'GET' ? await fetch('/api/auth/csrf').then(r=>r.json()) : null;
  if(csrf) options.headers={...options.headers,[csrf.headerName]:csrf.token};
  const response = await fetch('/api/' + path, { ...options, headers: {'Content-Type':'application/json', ...options.headers} });
  if (response.status === 204) return null;
  const data = await response.json().catch(()=>({}));
  if (!response.ok) throw Object.assign(new Error(formatFriendlyValidation(data, response.status)), {status:response.status, data});
  return data;
}
const schemas = {
 'sach': {title:'Quản lý sách', single:'sách', columns:['hinhAnh','maSach','tenSach','tacGia','giaBan','soLuongTon'], fields:[['maSach','Mã sách','text',true,30],['tenSach','Tên sách','text',true,255],['tacGia','Tác giả','text',true,150],['isbn','ISBN','text',false,20],['namXuatBan','Năm xuất bản','number'],['giaBan','Giá bán','number',true],['soLuongTon','Số lượng tồn','number',true],['danhMucId','Danh mục','select',true],['nhaXuatBanId','Nhà xuất bản','select',true],['hinhAnh','Đường dẫn hình ảnh','text',false,500],['moTa','Mô tả','textarea'],['trangThai','Trạng thái','boolean',true]]},
 'danh-muc': {title:'Quản lý danh mục',single:'danh mục',columns:['tenDanhMuc','moTa','trangThai'],fields:[['tenDanhMuc','Tên danh mục','text',true,100],['moTa','Mô tả','textarea',false,1000],['trangThai','Trạng thái','boolean',true]]},
 'nha-xuat-ban': {title:'Quản lý nhà xuất bản',single:'nhà xuất bản',columns:['tenNhaXuatBan','soDienThoai','email','trangThai'],fields:[['tenNhaXuatBan','Tên nhà xuất bản','text',true,150],['diaChi','Địa chỉ','text',false,500],['soDienThoai','Số điện thoại','tel',false,20],['email','Email','email',false,150],['trangThai','Trạng thái','boolean',true]]},
 'khach-hang': {title:'Quản lý khách hàng',single:'khách hàng',columns:['hoTen','soDienThoai','email','diaChi'],fields:[['hoTen','Họ tên','text',true,150],['soDienThoai','Số điện thoại','tel',false,20],['email','Email','email',false,150],['diaChi','Địa chỉ','text',false,500]]}
};
function showToastPopup(text, isWarning = false) {
  if (!text) return;
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  toast.className = 'toast-popup ' + (isWarning ? 'toast-warning' : 'toast-success');
  toast.innerHTML = '<span>' + esc(text) + '</span><button type="button" class="toast-close" title="Đóng">✕</button>';
  let closed = false;
  const close = () => {
    if (closed) return;
    closed = true;
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(-10px)';
    setTimeout(() => toast.remove(), 200);
  };
  toast.onclick = close;
  container.appendChild(toast);
  setTimeout(close, 5500);
}
function message(text, error=false, target=document.querySelector('#message')) { if(target) {target.textContent=text;target.className='notice'+(error?' warning-notice':'');target.hidden=!text;} if(text) showToastPopup(text, error); }
async function allPages(resource) {
 const first = await api(`${resource}?page=0&size=100`);
 const rows = [...first.content];
 for(let p=1;p<first.totalPages;p++) rows.push(...(await api(`${resource}?page=${p}&size=100`)).content);
 return rows;
}
function adminBookCover(book,publisherName){
 const imageUrl=cover(book);
 const fallback=bookArt({...book,hinhAnh:''},publisherName);
 return `<div class="admin-cover-cell"><div class="admin-cover-fallback" ${imageUrl?'aria-hidden="true"':''}>${fallback}</div>${imageUrl?`<img class="admin-cover-thumb" src="${imageUrl}" alt="Bìa ${esc(book.tenSach)}" loading="lazy">`:''}</div>`;
}
async function manager(resource, admin=false) {
 const config=schemas[resource]; let page=0, data, busy=false, publisherNames=null;
 const canWrite=currentUser.role==='ADMIN'||resource==='sach'||resource==='khach-hang';
 app.innerHTML=`<span class="eyebrow">${admin?'Không gian quản trị':'Dữ liệu nhà sách'}</span><h1>${config.title}</h1><p>${canWrite?'Thêm, cập nhật và tra cứu':'Tra cứu'} dữ liệu trực tiếp từ REST API.</p><div class="toolbar">${canWrite?`<button id="add">+ Thêm ${config.single}</button>`:''}<button id="refresh" class="secondary">Tải lại dữ liệu</button></div><div id="message" role="alert" hidden></div><div id="rows" class="table-wrap" aria-live="polite"></div><div class="pager"><button id="prev" class="secondary">← Trước</button><span id="page"></span><button id="next" class="secondary">Sau →</button></div><dialog aria-labelledby="dialog-title"><h2 id="dialog-title"></h2><form id="edit-form"><div id="fields" class="form-grid"></div><div id="form-error" role="alert" hidden></div><div class="actions"><button id="save" type="submit">Lưu dữ liệu</button><button id="cancel" type="button" class="secondary">Hủy</button></div></form></dialog>`;
 const dialog=document.querySelector('dialog'), form=document.querySelector('#edit-form');
 async function load(){
  document.querySelector('#rows').textContent='Đang tải dữ liệu…';
  try {
   const [pageData,publishers]=resource==='sach'
    ? await Promise.all([api(`${resource}?page=${page}&size=10`),publisherNames?Promise.resolve(null):allPages('nha-xuat-ban')])
    : [await api(`${resource}?page=${page}&size=10`),null];
   data=pageData;
   if(publishers)publisherNames=new Map(publishers.map(p=>[p.id,p.tenNhaXuatBan]));
   if(page>0 && !data.content.length){page--;return await load();}
   const label=k=>k==='hinhAnh'?'Bìa':config.fields.find(f=>f[0]===k)[1];
   const cell=(row,k)=>resource==='sach'&&k==='hinhAnh'?adminBookCover(row,publisherNames?.get(row.nhaXuatBanId)):k==='giaBan'?money(row[k]):k==='trangThai'?(row[k]?'Hoạt động':'Tạm ngừng'):esc(row[k]);
   document.querySelector('#rows').innerHTML=`<table><thead><tr>${config.columns.map(k=>`<th scope="col">${label(k)}</th>`).join('')}${canWrite?'<th scope="col">Thao tác</th>':''}</tr></thead><tbody>${data.content.map(row=>`<tr>${config.columns.map(k=>`<td>${cell(row,k)}</td>`).join('')}${canWrite?`<td><button data-edit="${row.id}" class="secondary">Sửa</button><button data-delete="${row.id}" class="danger">Xóa</button></td>`:''}</tr>`).join('') || `<tr><td colspan="${config.columns.length+(canWrite?1:0)}">Chưa có dữ liệu.</td></tr>`}</tbody></table>`;
   document.querySelectorAll('.admin-cover-thumb').forEach(img=>img.addEventListener('error',()=>{img.hidden=true;}));
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
 document.querySelector('#add')?.addEventListener('click',()=>edit());
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
const CART_PREFIX='qlns.cart.v2.';
const PENDING_ORDER_PREFIX='qlns.pending-order.v2.';
function accountStorageKey(prefix){return prefix+(currentUser.role==='KHACH_HANG'&&currentUser.username?currentUser.username:'guest');}
function cartKey(){return accountStorageKey(CART_PREFIX);}
function pendingOrderKey(){return accountStorageKey(PENDING_ORDER_PREFIX);}
function readCart(){try{const value=JSON.parse(localStorage.getItem(cartKey())||'{}');return value&&typeof value==='object'?value:{}}catch(e){return {};}}
function writeCart(cart){localStorage.setItem(cartKey(),JSON.stringify(cart));updateCartCount();}
function updateCartCount(){const count=Object.values(readCart()).reduce((sum,value)=>sum+Number(value||0),0);document.querySelectorAll('[data-cart-count]').forEach(el=>el.textContent=count);}
function cover(book){
 const url=typeof book.hinhAnh==='string'?book.hinhAnh.trim():'';
 return url && /^(\/images\/|https?:\/\/)/.test(url)?esc(url):'';
}
function bookArt(b,publisherName='',large=false,detail=false){
 const imageUrl=cover(b);
 if(imageUrl)return `<img class="${detail?'detail-cover':large?'feature-image':'cover'}" src="${imageUrl}" alt="Bìa ${esc(b.tenSach)}">`;
 const tones=['forest','navy','gold','plum','sage','clay','teal','indigo','rust','olive'];
 const categoryIndex=Math.max(0,Math.abs(Number(b.danhMucId)||1)-1);
 const tone=tones[categoryIndex%tones.length];
 const publisher=publisherName||'Nhà xuất bản đang cập nhật';
 return `<div class="book-art ${tone} ${large?'large':''} ${detail?'detail-art':''}" role="img" aria-label="Bìa minh họa ${esc(b.tenSach)} · ${esc(publisher)}"><small>TỦ SÁCH TRI THỨC</small><strong>${esc(b.tenSach)}</strong><span class="book-mark">✦</span><em>${esc(publisher)}</em></div>`;
}
function card(b,publisherName,rental){
  return `<article class="shop-card"><a class="cover-link" href="/chi-tiet.html?id=${b.id}">${bookArt(b,publisherName)}</a><div class="book-meta"><small>${esc(b.maSach)} · ${b.soLuongTon>0?'Có sẵn':'Tạm hết hàng'}</small><h3><a href="/chi-tiet.html?id=${b.id}">${esc(b.tenSach)}</a></h3><p class="book-author">${esc(b.tacGia)}</p><div class="book-pricing"><strong class="book-price">${money(b.giaBan)}</strong>${rental?`<span class="rental-tag">Thuê ${money(rental.giaThueNgay)}/ng</span>`:''}</div><div class="book-actions">${favoriteButton(b.id)}<button class="btn-add-cart" data-cart-add="${b.id}" ${b.soLuongTon<1?'disabled':''} aria-label="Thêm ${esc(b.tenSach)} vào giỏ">＋ Giỏ</button>${rental?`<a class="btn-rent" href="/thue-sach.html?book=${b.id}" aria-label="Thuê ${esc(b.tenSach)}">Thuê</a>`:''}<a class="btn-detail" href="/chi-tiet.html?id=${b.id}" aria-label="Xem chi tiết" title="Xem chi tiết">↗</a></div></div></article>`;
}
async function catalog(home=false){
 document.body.classList.add('storefront');
 app.innerHTML='<p role="status">Đang sắp xếp tủ sách…</p>';
 try{
 const [books,categories,publishers,rentals]=await Promise.all([allPages('sach'),allPages('danh-muc'),allPages('nha-xuat-ban'),api('rental-catalog').catch(()=>[])]);
 const rentalBooks=new Map(rentals.filter(r=>r.choThue).map(r=>[r.sachId,r]));
 const publisherNames=new Map(publishers.map(p=>[p.id,p.tenNhaXuatBan]));
 let selected=Number(new URLSearchParams(location.search).get('category'))||0, query=new URLSearchParams(location.search).get('q')||'',page=0;
 const available=books.filter(b=>b.trangThai);
 const feature=available.find(b=>b.danhMucId===2)||available[0];
 const chips=categories.filter(c=>c.trangThai).map((c,i)=>`<button class="category-chip" data-category="${c.id}"><span>${['▤','❧','◈','✦','☀','Aa','⚛','◎','▧','♡'][i%10]}</span>${esc(c.tenDanhMuc)}</button>`).join('');
 app.innerHTML=`<div class="shop-search"><span>Đọc một cuốn sách, mở một thế giới.</span><form id="search-books" role="search"><input name="q" aria-label="Tìm tên sách hoặc tác giả" placeholder="Bạn đang tìm cuốn sách nào?" value="${esc(query)}"><button>Tìm sách ↗</button></form></div>${home?`<section class="store-hero"><div class="hero-copy"><span class="eyebrow">GÓC NHỎ CHO NGƯỜI YÊU SÁCH</span><h1>Chậm lại một chút.<br><i>Đọc thêm một trang.</i></h1><p>Từ những câu chuyện chạm đến trái tim đến kiến thức mở ra góc nhìn mới. Cuốn sách tiếp theo của bạn đang ở đây.</p><a class="button" href="#book-shelf">Tìm cuốn sách của bạn <span>→</span></a><div class="hero-note"><span>✦</span> Tuyển tập các tựa sách kinh điển và bán chạy nhất dành cho bạn</div></div><div class="hero-display">${feature?`<a href="/chi-tiet.html?id=${feature.id}" class="hero-book">${bookArt(feature,publisherNames.get(feature.nhaXuatBanId),true)}</a><div class="featured-caption"><span>GỢI Ý TRÊN KỆ</span><strong>${esc(feature.tenSach)}</strong><a href="/chi-tiet.html?id=${feature.id}">Xem thông tin sách →</a></div>`:''}<span class="hero-orbit">MỘT CUỐN SÁCH<br>VẠN ĐIỀU HAY</span></div></section><div class="reading-strip"><span>▤ <strong>Chọn sách theo sở thích</strong><small>10 chủ đề cho nhiều góc nhìn</small></span><span>☷ <strong>Xem thông tin trước khi chọn</strong><small>Tác giả, ISBN và dữ liệu xuất bản</small></span><span>♡ <strong>Nuôi dưỡng thói quen đọc</strong><small>Mỗi ngày dành một chút cho bản thân</small></span></div>`:''}${currentUser.role!=='GUEST'?'<section id="favorite-summary" class="favorite-summary" aria-live="polite"></section>':''}<section class="category-section"><div class="section-heading"><div><span class="eyebrow">BẮT ĐẦU TỪ ĐIỀU BẠN THÍCH</span><h2>Hôm nay, bạn muốn đọc gì?</h2></div><button id="all-categories" class="text-button">Tất cả thể loại →</button></div><div class="category-list">${chips}</div></section><section id="book-shelf"><div class="section-heading"><div><span class="eyebrow">NHỮNG TRANG SÁCH ĐANG CHỜ BẠN</span><h2 id="shelf-title">Khám phá trên kệ</h2></div><label class="sort-label">Sắp xếp<select id="sort-books"><option value="default">Theo mã sách</option><option value="price-asc">Giá tăng dần</option><option value="price-desc">Giá giảm dần</option><option value="title">Tên sách A–Z</option></select></label></div><div id="result-summary" aria-live="polite"></div><div class="shop-grid" id="books"></div><div class="pager"><button id="prev" class="secondary">← Trước</button><span id="page"></span><button id="next" class="secondary">Sau →</button></div></section>${home?'<section class="editorial"><span class="eyebrow">DÀNH MỘT CHÚT THỜI GIAN CHO MÌNH</span><h2>Không cần đọc thật nhanh.<br>Chỉ cần bắt đầu.</h2><p>Một câu chuyện trước giờ ngủ, một ý tưởng cho ngày mới, hay một chủ đề bạn luôn tò mò. Hãy chọn cuốn sách khiến bạn muốn lật trang tiếp theo.</p><a class="button" href="/sach.html?category=4">Khám phá sách kỹ năng sống →</a></section>':''}<div class="store-end"><div><strong>NHÀ SÁCH TRI THỨC</strong><p>Đồng hành cùng những trang sách của bạn.</p></div><div><a href="/sach.html">Khám phá tủ sách</a><a href="/dang-nhap.html">Tài khoản bạn đọc</a></div></div>`;
 const normalize=v=>v.normalize('NFD').replace(/[\u0300-\u036f]/g,'').replace(/đ/g,'d').toLowerCase();
 function load(){let rows=available.filter(b=>(!selected||b.danhMucId===selected)&&normalize(b.tenSach+' '+b.tacGia).includes(normalize(query)));
 const sort=document.querySelector('#sort-books').value;if(sort==='price-asc')rows.sort((a,b)=>a.giaBan-b.giaBan);if(sort==='price-desc')rows.sort((a,b)=>b.giaBan-a.giaBan);if(sort==='title')rows.sort((a,b)=>a.tenSach.localeCompare(b.tenSach,'vi'));
 const pages=Math.ceil(rows.length/8);page=Math.max(0,Math.min(page,pages-1));
 document.querySelector('#books').innerHTML=rows.slice(page*8,page*8+8).map(b=>card(b,publisherNames.get(b.nhaXuatBanId),rentalBooks.get(b.id))).join('')||'<div class="empty-shelf"><h3>Chưa tìm thấy cuốn sách phù hợp</h3><p>Thử một tên sách, tác giả hoặc thể loại khác nhé.</p></div>';
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
function authorKey(value){return (value||'').normalize('NFC').trim().replace(/\s+/g,' ').toLocaleLowerCase('vi');}
async function relatedBooks(book,category,mode='category',catalogData){
 const sameAuthor=mode==='author';
 const sectionId=sameAuthor?'same-author-books':'related-books';
 const title=sameAuthor?'Sách cùng tác giả':'Sách cùng thể loại';
 const section=document.querySelector('#'+sectionId);
 if(!section)return;
 section.innerHTML=`<h2 id="${sectionId}-title">${title}</h2><p role="status">Đang tìm sách gợi ý…</p>`;
 try{
  const [books,publishers]=await (catalogData||Promise.all([allPages('sach'),allPages('nha-xuat-ban')]));
  const author=authorKey(book.tacGia);
  const suggestions=books.filter(item=>item.id!==book.id&&item.trangThai&&(sameAuthor
   ?author!==''&&authorKey(item.tacGia)===author:item.danhMucId===book.danhMucId))
   .sort((left,right)=>Number(right.soLuongTon>0)-Number(left.soLuongTon>0)||left.id-right.id).slice(0,4);
  const publisherNames=new Map(publishers.map(item=>[item.id,item.tenNhaXuatBan]));
  const description=sameAuthor?(author?`Khám phá thêm những cuốn sách của ${esc(book.tacGia)}.`:'Cuốn sách này chưa có thông tin tác giả.'):`Khám phá thêm những cuốn sách thuộc ${esc(category.tenDanhMuc)}.`;
  const empty=sameAuthor?'Hiện chưa có sách khác cùng tác giả. Bạn có thể khám phá thêm trong tủ sách.':'Hiện chưa có sách khác cùng thể loại. Bạn có thể khám phá thêm trong tủ sách.';
  section.innerHTML=`<div class="section-heading"><div><span class="eyebrow">CÓ THỂ BẠN CŨNG THÍCH</span><h2 id="${sectionId}-title">${title}</h2><p>${description}</p></div><a class="button secondary" href="/sach.html">Khám phá tủ sách →</a></div>${suggestions.length?`<div class="shop-grid">${suggestions.map(item=>card(item,publisherNames.get(item.nhaXuatBanId))).join('')}</div>`:`<p class="empty-shelf">${empty}</p>`}`;
 }catch(error){
  section.innerHTML=`<h2 id="${sectionId}-title">${title}</h2><p role="status">Chưa tải được sách gợi ý.</p><button type="button" id="retry-${sectionId}">Thử lại</button>`;
  section.querySelector('#retry-'+sectionId).onclick=()=>relatedBooks(book,category,mode);
 }
}
async function detail(){
 app.innerHTML='<p>Đang tải sách…</p>';
 try{const id=new URLSearchParams(location.search).get('id');if(!/^\d+$/.test(id||''))throw Error('Mã sách không hợp lệ');
 const b=await api('sach/'+id), c=await api('danh-muc/'+b.danhMucId),n=await api('nha-xuat-ban/'+b.nhaXuatBanId),rental=(await api('rental-catalog').catch(()=>[])).find(r=>r.sachId===b.id&&r.choThue);
 app.innerHTML=`<a href="/sach.html">← Quay lại tủ sách</a><section class="detail panel">${bookArt(b,n.tenNhaXuatBan,false,true)}<div><span class="eyebrow">${esc(c.tenDanhMuc)}</span><h1>${esc(b.tenSach)}</h1><p>Tác giả: <strong>${esc(b.tacGia)}</strong></p><p>Nhà xuất bản: ${esc(n.tenNhaXuatBan)}</p><p>Năm xuất bản: ${esc(b.namXuatBan||'Đang cập nhật')}</p><p>ISBN: ${esc(b.isbn||'Đang cập nhật')}</p><div class="price">${money(b.giaBan)}</div><p>${b.soLuongTon>0?'Còn '+b.soLuongTon+' cuốn':'Tạm hết hàng'}</p>${favoriteButton(b.id)} <button data-cart-add="${b.id}" ${b.soLuongTon<1?'disabled':''}>Mua sách</button>${rental?` <a class="button secondary" href="/thue-sach.html?book=${b.id}">Thuê sách · ${money(rental.giaThueNgay)}/ngày</a>`:''}</div></section><section class="panel"><h2>Giới thiệu sản phẩm</h2><p class="book-introduction">${esc(b.moTa||'Một đầu sách thuộc thể loại '+c.tenDanhMuc+'.')}</p></section><section class="panel publication-panel" id="publication"><h2>Thông tin xuất bản</h2><dl class="publication-details"><div><dt>Tên sách</dt><dd>${esc(b.tenSach)}</dd></div><div><dt>Mã sách</dt><dd>${esc(b.maSach)}</dd></div><div><dt>Tác giả / Biên soạn</dt><dd>${esc(b.tacGia||'Chưa có thông tin tác giả')}</dd></div><div><dt>Nhà xuất bản</dt><dd>${esc(n.tenNhaXuatBan)}</dd></div><div><dt>Năm xuất bản</dt><dd>${esc(b.namXuatBan||'Chưa có thông tin năm xuất bản')}</dd></div><div><dt>Thể loại</dt><dd>${esc(c.tenDanhMuc)}</dd></div><div><dt>Mã ISBN</dt><dd>${b.isbn?esc(b.isbn):'<span class="publication-missing">Chưa có thông tin ISBN</span>'}</dd></div></dl></section><section id="related-books" class="related-books" aria-labelledby="related-books-title" aria-live="polite"><h2 id="related-books-title">Sách cùng thể loại</h2></section><section id="same-author-books" class="related-books" aria-labelledby="same-author-books-title" aria-live="polite"><h2 id="same-author-books-title">Sách cùng tác giả</h2></section>`;
 const catalogData=Promise.all([allPages('sach'),allPages('nha-xuat-ban')]);
 await Promise.all([relatedBooks(b,c,'category',catalogData),relatedBooks(b,c,'author',catalogData)]);
 }catch(e){app.innerHTML=`<p role="alert">${esc(e.message)}</p><a href="/sach.html">Về tủ sách</a>`;}
}
function login(admin=false,register=false){
 app.innerHTML=`<section class="panel login"><span class="eyebrow">${admin?'Khu vực quản trị':'Nhà sách Tri Thức'}</span><h1>${register?'Tạo tài khoản':admin?'Đăng nhập admin':'Đăng nhập bạn đọc'}</h1><form id="login-form">${register?'<label>Họ tên<input name="hoTen" required maxlength="150"></label><label>Email<input name="email" type="email" required maxlength="150"></label>':''}<label>Tên đăng nhập<input name="username" required autocomplete="username" pattern="[a-zA-Z0-9_]{4,50}"></label><label>Mật khẩu<input name="password" type="password" required minlength="8" maxlength="72" autocomplete="${register?'new-password':'current-password'}"></label><button type="submit">${register?'Đăng ký':'Đăng nhập'}</button></form><div id="message" role="alert" hidden></div><p>${register?'<a href="/dang-nhap.html">Đã có tài khoản? Đăng nhập</a>':admin?'<a href="/dang-nhap.html">Đăng nhập dành cho bạn đọc</a>':'<a href="/dang-ky.html">Tạo tài khoản bạn đọc</a> · <a href="/admin-login.html">Dành cho admin</a>'}</p></section>`;
 document.querySelector('#login-form').onsubmit=async e=>{e.preventDefault();const button=e.currentTarget.querySelector('button');button.disabled=true;try{const values=Object.fromEntries(new FormData(e.currentTarget));values.username=values.username.trim();if(register){await api('auth/register',{method:'POST',body:JSON.stringify(values)});location.href='/dang-nhap.html?registered=1';return;}
 const csrf=await api('auth/csrf');const res=await fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded',[csrf.headerName]:csrf.token},body:new URLSearchParams(values)});const data=await res.json().catch(()=>({}));if(res.status===404)throw Error('Phiên ứng dụng này chưa có chức năng đăng nhập. Hãy mở bản mới hoặc khởi động lại ứng dụng.');if(res.status===403)throw Error('Phiên đăng nhập đã hết hạn. Hãy tải lại trang rồi đăng nhập lại.');if(!res.ok)throw Error(data.message||'Đăng nhập thất bại');
 if(admin&&!isStaffRole(data.role)){await api('auth/logout',{method:'POST'});throw Error('Tài khoản này không có quyền quản trị');}
 location.href=isStaffRole(data.role)?'/quan-tri.html':'/sach.html';
 }catch(err){message(err.status===404?'Phiên ứng dụng này chưa có chức năng đăng nhập. Hãy mở bản mới hoặc khởi động lại ứng dụng.':err.message,true);}finally{button.disabled=false;}};
 if(new URLSearchParams(location.search).has('registered'))message('Đăng ký thành công. Hãy đăng nhập.');
}
function favoriteButton(id){if(isStaffRole(currentUser.role))return '';const saved=favoriteIds.has(id);return `<button class="favorite-button ${saved?'saved':''}" data-favorite="${id}" aria-pressed="${saved}" aria-label="${saved?'Bỏ khỏi':'Thêm vào'} sách yêu thích">${saved?'♥':'♡'}</button>`;}
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
document.addEventListener('click',e=>{
 const button=e.target.closest('[data-cart-add]');if(!button)return;
 if(currentUser.role!=='KHACH_HANG'){location.href='/dang-nhap.html';return;}
 const cart=readCart(),id=String(button.dataset.cartAdd);cart[id]=Math.min(99,Number(cart[id]||0)+1);writeCart(cart);
 const old=button.textContent;button.textContent='Đã thêm ✓';setTimeout(()=>button.textContent=old,900);
});

async function cartPage(){
 if(currentUser.role!=='KHACH_HANG'){location.href='/dang-nhap.html';return;}
 document.body.classList.add('storefront');app.innerHTML='<p>Đang tải giỏ hàng…</p>';
 try{
  const [books,profile]=await Promise.all([allPages('sach'),api('ho-so')]),byId=new Map(books.map(b=>[String(b.id),b]));
  const render=()=>{
   const cart=readCart(),lines=Object.entries(cart).map(([id,quantity])=>({book:byId.get(id),quantity:Number(quantity)})).filter(x=>x.book&&x.quantity>0);
   const total=lines.reduce((sum,line)=>sum+line.book.giaBan*line.quantity,0);
   app.innerHTML=`<h1>Giỏ hàng</h1><div id="message" role="alert" hidden></div>${lines.length?`<div class="table-wrap"><table><thead><tr><th>Sách</th><th>Giá</th><th>Số lượng</th><th>Thành tiền</th><th></th></tr></thead><tbody>${lines.map(line=>`<tr><td>${esc(line.book.tenSach)}</td><td>${money(line.book.giaBan)}</td><td><input class="cart-qty" data-cart-id="${line.book.id}" type="number" min="1" max="${Math.min(99,line.book.soLuongTon)}" value="${line.quantity}"></td><td>${money(line.book.giaBan*line.quantity)}</td><td><button class="danger" data-cart-remove="${line.book.id}">Xóa</button></td></tr>`).join('')}</tbody></table></div><section class="panel checkout"><h2>Thông tin nhận hàng</h2><p class="price">Tổng cộng: ${money(total)}</p><form id="checkout-form"><label>Người nhận<input name="tenNguoiNhan" required maxlength="150" value="${esc(profile.hoTen)}"></label><label>Số điện thoại<input name="soDienThoaiNhan" required maxlength="20" pattern="\\+?[0-9][0-9 .-]{6,18}[0-9]" value="${esc(profile.soDienThoai)}"></label><label>Địa chỉ giao<input name="diaChiGiao" required maxlength="500" value="${esc(profile.diaChi)}"></label><label>Ghi chú<textarea name="ghiChu" maxlength="1000"></textarea></label><button id="checkout-submit">Đặt hàng và nhận mã QR</button></form></section>`:'<section class="empty-shelf"><h2>Giỏ hàng đang trống</h2><a class="button" href="/sach.html">Chọn sách →</a></section>'}`;
   document.querySelectorAll('[data-cart-remove]').forEach(button=>button.onclick=()=>{const value=readCart();delete value[button.dataset.cartRemove];writeCart(value);render();});
   document.querySelectorAll('[data-cart-id]').forEach(input=>input.onchange=()=>{const value=readCart(),max=Number(input.max),quantity=Math.max(1,Math.min(max,Number(input.value)||1));value[input.dataset.cartId]=quantity;writeCart(value);render();});
   document.querySelector('#checkout-form')?.addEventListener('submit',async event=>{
    event.preventDefault();const button=document.querySelector('#checkout-submit');button.disabled=true;
    const current=readCart(),items=Object.entries(current).map(([sachId,soLuong])=>({sachId:Number(sachId),soLuong:Number(soLuong)}));
    const snapshot=JSON.stringify(items),pendingKey=pendingOrderKey(),stored=JSON.parse(localStorage.getItem(pendingKey)||'null');
    const pending=stored&&stored.snapshot===snapshot?stored:{snapshot,token:crypto.randomUUID()};localStorage.setItem(pendingKey,JSON.stringify(pending));
    try{const values=Object.fromEntries(new FormData(event.currentTarget));const order=await api('orders',{method:'POST',body:JSON.stringify({...values,clientRequestId:pending.token,items})});
     localStorage.removeItem(cartKey());localStorage.removeItem(pendingKey);location.href='/don-hang.html?created='+order.id;
    }catch(err){message(err.message,true);button.disabled=false;}
   });
  };render();
 }catch(err){app.innerHTML=`<section class="panel"><h1>Không tải được giỏ hàng</h1><p role="alert">${esc(err.message)}</p></section>`;}
}

const orderLabels={CHO_XAC_NHAN:'Chờ xác nhận',DA_XAC_NHAN:'Đã xác nhận',DANG_GIAO:'Đang giao',HOAN_THANH:'Hoàn thành',DA_HUY:'Đã hủy'};
const orderActionLabels={DA_XAC_NHAN:'Xác nhận đơn',DANG_GIAO:'Bắt đầu giao',HOAN_THANH:'Hoàn thành đơn',DA_HUY:'Hủy đơn'};
function orderPayment(payment){
 if(!payment)return '';
 const pending=['CHO_THANH_TOAN','CHO_DOI_SOAT'].includes(payment.trangThai),showQr=pending&&payment.qrConfigured&&payment.qrImageUrl;
 const note=showQr?'Quét mã để chuyển đúng số tiền và nội dung. Đơn chỉ được ghi nhận đã thanh toán sau khi nhân viên đối soát.':(payment.qrNote||'Khoản thanh toán đã được cập nhật.');
 return `<section class="payment-box"><div><span class="eyebrow">THANH TOÁN ${esc(payment.maThanhToan)}</span><h3>${money(payment.soTien)}</h3><p>Nội dung chuyển khoản: <strong>${esc(payment.noiDungChuyenKhoan)}</strong></p><span class="status-badge">${esc(payment.trangThai)}</span></div>${showQr?`<img src="${esc(payment.qrImageUrl)}" alt="Mã VietQR cho đơn hàng">`:''}<p class="payment-note">${esc(note)}</p></section>`;
}
function pager(data,page){return `<div class="pager"><button id="prev" class="secondary" ${page===0?'disabled':''}>← Trước</button><span>Trang ${data.totalPages?page+1:0}/${data.totalPages} · ${data.totalElements} bản ghi</span><button id="next" class="secondary" ${page+1>=data.totalPages?'disabled':''}>Sau →</button></div>`;}
function bindPager(data,page,go){document.querySelector('#prev').onclick=()=>go(page-1);document.querySelector('#next').onclick=()=>go(page+1);}
async function ordersPage(admin=false,page=0){
 app.innerHTML='<p>Đang tải đơn hàng…</p>';
 try{
  const data=await api(`orders?page=${page}&size=12`);
  if(page>0&&!data.content.length)return ordersPage(admin,page-1);
   app.innerHTML=`<span class="eyebrow">${admin?'VẬN HÀNH':'TÀI KHOẢN CỦA BẠN'}</span><h1>${admin?'Quản lý đơn hàng':'Đơn hàng của tôi'}</h1><div id="message" role="alert" hidden></div><div class="order-list">${data.content.map(order=>`<article class="panel order-card"><div class="section-heading"><div><strong>${esc(order.maDonHang)}</strong><small>${new Date(order.ngayDat).toLocaleString('vi-VN')}</small></div><span class="status-badge">${orderLabels[order.trangThai]||esc(order.trangThai)}</span></div><p>${esc(order.tenNguoiNhan)} · ${esc(order.soDienThoaiNhan)} · ${esc(order.diaChiGiao)}</p><ul>${order.items.map(item=>`<li>${esc(item.tenSach)} × ${item.soLuong} — ${money(item.thanhTien)}</li>`).join('')}</ul><strong>${money(order.tongTien)}</strong>${orderPayment(order.payment)}<div class="actions">${orderActions(order,admin)}</div></article>`).join('')||'<section class="empty-shelf"><h2>Chưa có đơn hàng</h2></section>'}</div>${pager(data,page)}`;
  bindPager(data,page,next=>ordersPage(admin,next));
  document.querySelectorAll('[data-order-status]').forEach(button=>button.onclick=async()=>{button.disabled=true;try{await api(`orders/${button.dataset.orderId}/status`,{method:'PATCH',body:JSON.stringify({trangThai:button.dataset.orderStatus})});await ordersPage(admin,page);}catch(err){message(err.message,true);button.disabled=false;}});
 }catch(err){app.innerHTML=`<section class="panel"><h1>Không tải được đơn hàng</h1><p role="alert">${esc(err.message)}</p></section>`;}
}
function orderActions(order,admin){
 const states=admin?({CHO_XAC_NHAN:['DA_XAC_NHAN','DA_HUY'],DA_XAC_NHAN:['DANG_GIAO','DA_HUY'],DANG_GIAO:['HOAN_THANH']}[order.trangThai]||[]):(order.trangThai==='CHO_XAC_NHAN'?['DA_HUY']:[]);
 return states.map(state=>`<button data-order-id="${order.id}" data-order-status="${state}" class="${state==='DA_HUY'?'danger':'secondary'}">${orderActionLabels[state]}</button>`).join('');
}

async function staffAccountsPage(page=0){
 if(currentUser.role!=='ADMIN'){app.innerHTML='<p role="alert">Chỉ quản trị viên được quản lý tài khoản nhân viên.</p>';return;}
 app.innerHTML='<p>Đang tải tài khoản nhân viên…</p>';
 try{
  const data=await api(`accounts?page=${page}&size=20`);
  if(page>0&&!data.content.length)return staffAccountsPage(page-1);
  app.innerHTML=`<h1>Tài khoản nhân viên</h1><div id="message" role="alert" hidden></div><section class="panel"><h2>Tạo nhân viên</h2><form id="staff-create" class="form-grid"><label>Tên đăng nhập<input name="username" required pattern="[a-zA-Z0-9_]{3,50}"></label><label>Email<input name="email" type="email" required maxlength="150"></label><label>Mật khẩu ban đầu<input name="password" type="password" required minlength="8" maxlength="64"></label><button>Tạo tài khoản</button></form></section><div class="table-wrap"><table><thead><tr><th>Tên đăng nhập</th><th>Email</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>${data.content.map(row=>`<tr><td>${esc(row.username)}</td><td>${esc(row.email)}</td><td>${row.enabled?'Hoạt động':'Đã khóa'}</td><td><button data-staff-toggle="${row.id}" data-enabled="${row.enabled}" data-email="${esc(row.email)}" class="secondary">${row.enabled?'Khóa':'Mở khóa'}</button> <button data-staff-password="${row.id}" class="secondary">Đặt lại mật khẩu</button></td></tr>`).join('')||'<tr><td colspan="4">Chưa có nhân viên.</td></tr>'}</tbody></table></div>${pager(data,page)}`;
  bindPager(data,page,staffAccountsPage);
  document.querySelector('#staff-create').onsubmit=async event=>{event.preventDefault();const button=event.currentTarget.querySelector('button');button.disabled=true;try{await api('accounts',{method:'POST',body:JSON.stringify(Object.fromEntries(new FormData(event.currentTarget)))});await staffAccountsPage(0);}catch(err){message(err.message,true);button.disabled=false;}};
  document.querySelectorAll('[data-staff-toggle]').forEach(button=>button.onclick=async()=>{button.disabled=true;try{await api('accounts/'+button.dataset.staffToggle,{method:'PUT',body:JSON.stringify({email:button.dataset.email,enabled:button.dataset.enabled!=='true'})});await staffAccountsPage(page);}catch(err){message(err.message,true);button.disabled=false;}});
  document.querySelectorAll('[data-staff-password]').forEach(button=>button.onclick=async()=>{const password=prompt('Nhập mật khẩu mới (8-64 ký tự):');if(password===null)return;try{await api(`accounts/${button.dataset.staffPassword}/password`,{method:'PUT',body:JSON.stringify({password})});message('Đã đặt lại mật khẩu.');}catch(err){message(err.message,true);}});
 }catch(err){app.innerHTML=`<section class="panel"><h1>Không tải được nhân viên</h1><p role="alert">${esc(err.message)}</p></section>`;}
}
async function uploadImage(file,type){
 if(file.size>5*1024*1024)throw Error('Ảnh tối đa 5 MB');
 const csrf=await api('auth/csrf'),body=new FormData();body.append('file',file);
 const res=await fetch('/api/anh/'+type,{method:'POST',headers:{[csrf.headerName]:csrf.token},body});
 const data=await res.json().catch(()=>({}));if(!res.ok)throw Error(data.message||'Không tải được ảnh. Hãy thử ảnh JPG/PNG dưới 5 MB.');return data.url;
}
async function profilePage(){
 app.innerHTML='<p role="status">Đang tải thông tin cá nhân…</p>';
 try{
  let data=await api('ho-so');

  function renderGenres(genres){
   if(!genres || !genres.trim()) return '<span class="text-muted">Chưa cập nhật thể loại yêu thích</span>';
   const tags=genres.split(/[,;\n]+/).map(t=>t.trim()).filter(Boolean);
   if(!tags.length) return '<span class="text-muted">Chưa cập nhật thể loại yêu thích</span>';
   return tags.map(t=>`<span class="genre-tag">${esc(t)}</span>`).join('');
  }

  app.innerHTML=`<span class="eyebrow">KHÔNG GIAN CỦA BẠN</span>
<h1>Trang cá nhân</h1>
<div class="profile-layout">
 <aside class="panel profile-aside">
  <div id="avatar-placeholder" class="avatar-placeholder">${esc(data.tenDangNhap[0].toUpperCase())}</div>
  <img id="avatar-preview" class="avatar" alt="Ảnh đại diện" hidden>
  <h2>${esc(data.tenDangNhap)}</h2>
  <span class="profile-role-tag">Bạn đọc thành viên</span>
  <div id="avatar-upload-box" class="avatar-upload-box" hidden>
   <label class="avatar-label">Tải ảnh đại diện mới
    <input id="avatar-file" type="file" accept="image/png,image/jpeg">
   </label>
   <p class="field-hint" style="margin:6px 0 0">JPG/PNG, tối đa 5 MB.</p>
  </div>
  <hr style="border:0;border-top:1px solid #e7ece2;margin:16px 0">
  <a class="profile-side-link" href="/yeu-thich.html">♡ Sách yêu thích</a>
  <a class="profile-side-link" href="/don-hang.html">📦 Đơn hàng của bạn</a>
  <a class="profile-side-link" href="/thue-sach.html">↻ Phiếu thuê sách</a>
 </aside>

 <section class="panel">
  <!-- CHẾ ĐỘ XEM (VIEW MODE) MẶC ĐỊNH -->
  <div id="profile-view-box">
   <div class="profile-panel-header">
    <div>
     <h2>Thông tin cá nhân</h2>
     <p>Xem thông tin tài khoản và thông tin liên hệ của bạn.</p>
    </div>
    <button id="btn-start-edit" class="button" type="button">✏️ Chỉnh sửa hồ sơ</button>
   </div>
   <div id="view-message" role="status" hidden></div>
   <div class="profile-info-grid">
    <div class="info-item">
     <small>Tên đăng nhập</small>
     <strong>${esc(data.tenDangNhap)}</strong>
    </div>
    <div class="info-item">
     <small>Họ và tên</small>
     <strong id="view-hoTen">${data.hoTen ? esc(data.hoTen) : '<span class="text-muted">Chưa cập nhật</span>'}</strong>
    </div>
    <div class="info-item">
     <small>Địa chỉ email</small>
     <strong id="view-email">${data.email ? esc(data.email) : '<span class="text-muted">Chưa cập nhật</span>'}</strong>
    </div>
    <div class="info-item">
     <small>Số điện thoại</small>
     <strong id="view-soDienThoai">${data.soDienThoai ? esc(data.soDienThoai) : '<span class="text-muted">Chưa cập nhật</span>'}</strong>
    </div>
    <div class="info-item full-width">
     <small>Địa chỉ nhận hàng</small>
     <strong id="view-diaChi">${data.diaChi ? esc(data.diaChi) : '<span class="text-muted">Chưa cập nhật</span>'}</strong>
    </div>
    <div class="info-item full-width">
     <small>Thể loại sách yêu thích</small>
     <div id="view-theLoai" class="genre-tags">${renderGenres(data.theLoaiYeuThich)}</div>
    </div>
   </div>
  </div>

  <!-- CHẾ ĐỘ CHỈNH SỬA (EDIT MODE - MẶC ĐỊNH ẨN) -->
  <div id="profile-edit-box" hidden>
   <div class="profile-panel-header">
    <div>
     <h2>Chỉnh sửa thông tin cá nhân</h2>
     <p>Cập nhật họ tên, địa chỉ nhận hàng và thể loại quan tâm.</p>
    </div>
   </div>
   <form id="profile-form">
    <div class="form-grid">
     <label>Tên đăng nhập
      <input value="${esc(data.tenDangNhap)}" readonly disabled style="background:#f3f5ee;color:#6b7280;cursor:not-allowed">
     </label>
     <label>Họ tên *
      <input name="hoTen" id="input-hoTen" value="${esc(data.hoTen)}" required maxlength="150" autocomplete="name">
     </label>
     <label>Email *
      <input name="email" id="input-email" value="${esc(data.email)}" type="email" required maxlength="150" autocomplete="email">
     </label>
     <label>Số điện thoại
      <input name="soDienThoai" id="input-soDienThoai" value="${esc(data.soDienThoai)}" type="tel" pattern="[0-9+() .-]{8,20}" maxlength="20" autocomplete="tel">
     </label>
    </div>
    <label>Địa chỉ nhận hàng
     <input name="diaChi" id="input-diaChi" value="${esc(data.diaChi)}" maxlength="500" autocomplete="street-address">
    </label>
    <label>Thể loại yêu thích của bạn
     <textarea name="theLoaiYeuThich" id="input-theLoai" maxlength="500" placeholder="Ví dụ: Văn học, Kinh doanh, Khoa học…">${esc(data.theLoaiYeuThich)}</textarea>
    </label>
    <p class="field-hint">Nhập các thể loại bạn thích, phân cách bằng dấu phẩy.</p>
    <input name="anhDaiDien" id="input-anhDaiDien" type="hidden" value="${esc(data.anhDaiDien)}">
    <div id="edit-message" role="status" hidden></div>
    <div class="profile-edit-actions">
     <button id="profile-save" type="submit">💾 Lưu thay đổi</button>
     <button id="btn-cancel-edit" class="secondary" type="button">✕ Hủy bỏ</button>
    </div>
   </form>
  </div>
 </section>
</div>
<section id="favorite-summary" class="favorite-summary"></section>`;

  const viewBox=document.querySelector('#profile-view-box');
  const editBox=document.querySelector('#profile-edit-box');
  const avatarUploadBox=document.querySelector('#avatar-upload-box');
  const btnStartEdit=document.querySelector('#btn-start-edit');
  const btnCancelEdit=document.querySelector('#btn-cancel-edit');
  const form=document.querySelector('#profile-form');
  const saveBtn=document.querySelector('#profile-save');
  const avatarFile=document.querySelector('#avatar-file');
  const viewNotice=document.querySelector('#view-message');
  const editNotice=document.querySelector('#edit-message');

  function updateAvatarPreview(url){
   const previewEl=document.querySelector('#avatar-preview');
   const placeholderEl=document.querySelector('#avatar-placeholder');
   previewEl.hidden=!url;
   placeholderEl.hidden=!!url;
   if(url) previewEl.src=url;
  }
  updateAvatarPreview(data.anhDaiDien);

  function enterEditMode(){
   viewBox.hidden=true;
   editBox.hidden=false;
   avatarUploadBox.hidden=false;
   form.elements.hoTen.value=data.hoTen||'';
   form.elements.email.value=data.email||'';
   form.elements.soDienThoai.value=data.soDienThoai||'';
   form.elements.diaChi.value=data.diaChi||'';
   form.elements.theLoaiYeuThich.value=data.theLoaiYeuThich||'';
   form.elements.anhDaiDien.value=data.anhDaiDien||'';
   updateAvatarPreview(data.anhDaiDien);
   editNotice.hidden=true;
  }

  function exitEditMode(){
   editBox.hidden=true;
   avatarUploadBox.hidden=true;
   viewBox.hidden=false;
   updateAvatarPreview(data.anhDaiDien);
  }

  btnStartEdit.onclick=enterEditMode;
  btnCancelEdit.onclick=exitEditMode;

  avatarFile.onchange=async e=>{
   if(!e.target.files[0])return;
   saveBtn.disabled=true;
   try{
    const url=await uploadImage(e.target.files[0],'dai-dien');
    form.elements.anhDaiDien.value=url;
    updateAvatarPreview(url);
    message('Ảnh đại diện đã được tải lên tạm thời. Bấm "Lưu thay đổi" để áp dụng.',false,editNotice);
   }catch(err){
    message(err.message,true,editNotice);
   }finally{
    saveBtn.disabled=false;
   }
  };

  form.onsubmit=async e=>{
   e.preventDefault();
   saveBtn.disabled=true;
   try{
    const payload=Object.fromEntries(new FormData(form));
    const updated=await api('ho-so',{method:'PUT',body:JSON.stringify(payload)});
    data=updated;
    document.querySelector('#view-hoTen').innerHTML=data.hoTen?esc(data.hoTen):'<span class="text-muted">Chưa cập nhật</span>';
    document.querySelector('#view-email').innerHTML=data.email?esc(data.email):'<span class="text-muted">Chưa cập nhật</span>';
    document.querySelector('#view-soDienThoai').innerHTML=data.soDienThoai?esc(data.soDienThoai):'<span class="text-muted">Chưa cập nhật</span>';
    document.querySelector('#view-diaChi').innerHTML=data.diaChi?esc(data.diaChi):'<span class="text-muted">Chưa cập nhật</span>';
    document.querySelector('#view-theLoai').innerHTML=renderGenres(data.theLoaiYeuThich);
    updateAvatarPreview(data.anhDaiDien);
    currentUser.avatar = data.anhDaiDien;
    const navDot = document.querySelector('.user-menu-btn .user-avatar-dot');
    if (navDot) {
      if (data.anhDaiDien) {
        navDot.innerHTML = '<img src="' + esc(data.anhDaiDien) + '" alt="' + esc(currentUser.username) + '" onerror="this.parentElement.textContent=\'' + esc(currentUser.username[0].toUpperCase()) + '\'">';
      } else {
        navDot.textContent = currentUser.username[0].toUpperCase();
      }
    }
    exitEditMode();
    message('Đã cập nhật thông tin cá nhân thành công.',false,viewNotice);
   }catch(err){
    message(err.message,true,editNotice);
   }finally{
    saveBtn.disabled=false;
   }
  };

  await renderFavoriteSummary();
 }catch(e){
  app.innerHTML='<section class="panel"><h1>Chưa tải được hồ sơ</h1><p>Hãy đăng nhập lại hoặc tải lại trang để thử lại.</p><a href="/dang-nhap.html">Đăng nhập</a></section>';
 }
}
function renderUserAvatarDot(u) {
  if (u && u.avatar) {
    return '<span class="user-avatar-dot"><img src="' + esc(u.avatar) + '" alt="' + esc(u.username) + '" onerror="this.parentElement.textContent=\'' + esc(u.username[0].toUpperCase()) + '\'"></span>';
  }
  return '<span class="user-avatar-dot ' + (isStaffRole(u ? u.role : '') ? 'staff' : '') + '">' + esc(u ? u.username[0].toUpperCase() : 'U') + '</span>';
}
async function start(){
 const path=location.pathname;try{currentUser=await api('auth/me');}catch(e){}
 if(currentUser.role==='KHACH_HANG'){try{favoriteIds=new Set(await api('yeu-thich/ids'));}catch(e){favoriteIds=new Set();}}
 let actionsHtml='';
 if(currentUser.role==='KHACH_HANG'){
  actionsHtml=`<a href="/yeu-thich.html" class="nav-icon-btn" title="Sách yêu thích" aria-label="Sách yêu thích">♡</a><a href="/gio-hang.html" class="nav-icon-btn" title="Giỏ hàng" aria-label="Giỏ hàng">🛒<span class="cart-badge" data-cart-count>0</span></a><div class="user-menu-wrapper"><button class="user-menu-btn" id="user-menu-btn" type="button" aria-expanded="false" aria-haspopup="true">${renderUserAvatarDot(currentUser)}<span>${esc(currentUser.username)}</span><span class="user-caret">▼</span></button><div class="user-dropdown-box" id="user-menu" hidden><div class="user-dropdown-header"><strong>${esc(currentUser.username)}</strong><small>Bạn đọc thành viên</small></div><a href="/ca-nhan.html">👤 Thông tin cá nhân</a><a href="/don-hang.html">📦 Đơn hàng của bạn</a><a href="/yeu-thich.html">♡ Sách yêu thích</a><hr><button id="logout" class="user-dropdown-logout" type="button">⎋ Đăng xuất</button></div></div>`;
 } else if(isStaffRole(currentUser.role)){
  actionsHtml=`<div class="user-menu-wrapper"><button class="user-menu-btn" id="user-menu-btn" type="button" aria-expanded="false" aria-haspopup="true">${renderUserAvatarDot(currentUser)}<span>${esc(currentUser.username)} · ${currentUser.role==='ADMIN'?'Admin':'NV'}</span><span class="user-caret">▼</span></button><div class="user-dropdown-box" id="user-menu" hidden><div class="user-dropdown-header"><strong>${esc(currentUser.username)}</strong><small>${currentUser.role==='ADMIN'?'Quản trị hệ thống':'Nhân viên'}</small></div><a href="/quan-tri.html">⚙️ Trang quản trị</a><hr><button id="logout" class="user-dropdown-logout" type="button">⎋ Đăng xuất</button></div></div>`;
 } else {
  actionsHtml=`<a href="/dang-nhap.html" class="nav-login-link">Đăng nhập</a>`;
 }
 const navEl=document.querySelector('nav');
 if(navEl){
  navEl.innerHTML=`<div class="nav-links"><a href="/" ${path==='/'||path==='/index.html'?'aria-current="page"':''}>Trang chủ</a><a href="/sach.html" ${path==='/sach.html'?'aria-current="page"':''}>Tủ sách</a><a href="/thue-sach.html" ${path==='/thue-sach.html'?'aria-current="page"':''}>Thuê sách</a><a href="/sach-cu.html" ${path==='/sach-cu.html'?'aria-current="page"':''}>Sách cũ</a>${isStaffRole(currentUser.role)?`<a href="/quan-tri.html" ${path==='/quan-tri.html'?'aria-current="page"':''}>Quản trị</a>`:''}</div><div class="nav-actions">${actionsHtml}</div>`;
  updateCartCount();
  const userMenuBtn=document.querySelector('#user-menu-btn');
  const userMenuBox=document.querySelector('#user-menu');
  if(userMenuBtn&&userMenuBox){
   userMenuBtn.onclick=(e)=>{e.stopPropagation();const isHidden=userMenuBox.hidden;userMenuBox.hidden=!isHidden;userMenuBtn.setAttribute('aria-expanded',String(!isHidden));};
   document.addEventListener('click',(e)=>{if(!userMenuBox.contains(e.target)&&!userMenuBtn.contains(e.target)){userMenuBox.hidden=true;userMenuBtn.setAttribute('aria-expanded','false');}});
  }
 }
 document.querySelector('#logout')?.addEventListener('click',async()=>{await api('auth/logout',{method:'POST'});location.href='/';});
 if(path==='/dang-nhap.html'||path==='/admin-login.html'||path==='/dang-ky.html')return login(path==='/admin-login.html',path==='/dang-ky.html');
 if(path==='/ca-nhan.html')return profilePage();
 if(path==='/yeu-thich.html')return favoritesPage();
 if(path==='/gio-hang.html')return cartPage();
 if(path==='/don-hang.html')return ordersPage(false);
 if(path==='/thue-sach.html')return rentalPage();
 if(path==='/sach-cu.html')return usedBookPage();
 if(path==='/chi-tiet.html')return detail();
 if(path==='/quan-tri.html'||path==='/khach-hang.html'){
 if(!isStaffRole(currentUser.role)){location.href='/admin-login.html';return;}
 document.body.classList.add('admin-layout');
 const aside=document.createElement('aside');aside.className='admin-sidebar';aside.innerHTML=`<h2>NHÀ SÁCH ${currentUser.role==='ADMIN'?'ADMIN':'NHÂN VIÊN'}</h2><p>Không gian vận hành</p><a href="/quan-tri.html?module=sach">▤ Sách</a><a href="/quan-tri.html?module=danh-muc">☷ Danh mục</a><a href="/quan-tri.html?module=nha-xuat-ban">▣ Nhà xuất bản</a><a href="/quan-tri.html?module=khach-hang">♙ Khách hàng</a><a href="/quan-tri.html?module=orders">▦ Đơn hàng</a><a href="/quan-tri.html?module=rental">↻ Thuê & trả</a><a href="/quan-tri.html?module=memberships">◇ Hội viên</a><a href="/quan-tri.html?module=payments">₫ Đối soát</a><a href="/quan-tri.html?module=used-books">♲ Sách cũ</a>${currentUser.role==='ADMIN'?'<a href="/quan-tri.html?module=staff">♟ Nhân viên</a>':''}<a href="/">← Xem website</a>`;document.body.prepend(aside);
 const module=path==='/khach-hang.html'?'khach-hang':new URLSearchParams(location.search).get('module')||'sach';
 if(module==='orders')return ordersPage(true);if(module==='staff')return staffAccountsPage();if(module==='rental')return adminRentalPage();if(module==='memberships')return adminMembershipPage();if(module==='payments')return adminPaymentPage();if(module==='used-books')return adminUsedBooksPage();return manager(Object.hasOwn(schemas,module)?module:'sach',true);}
 return catalog(path==='/'||path==='/index.html');
}
start();
