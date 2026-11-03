'use strict';
function adminNotice(text,error=false){const el=document.querySelector('#admin-rental-message');message(text,error,el);if(el&&text)el.scrollIntoView({behavior:'smooth',block:'center'});}
function adminRentalRow(rental){
 const actions=[];
 if(rental.trangThai==='CHO_XAC_NHAN')actions.push(`<button data-admin-rental="approve" data-id="${rental.id}">Duyệt phiếu</button>`);
 if(rental.trangThai==='CHO_NHAN_SACH')actions.push(`<button data-admin-rental="handover" data-id="${rental.id}">Xác nhận giao sách</button>`);
 if(rental.trangThai==='DANG_THUE')actions.push(`<button data-admin-rental="return" data-id="${rental.id}">Nhận trả sách</button>`);
 if(['CHO_XAC_NHAN','CHO_THANH_TOAN','CHO_NHAN_SACH'].includes(rental.trangThai))actions.push(`<button class="danger" data-admin-rental="cancel" data-id="${rental.id}">Hủy</button>`);
 return `<article class="panel admin-rental-card"><div class="rental-card-head"><div><span class="eyebrow">${esc(rental.maPhieuThue)} · ${rental.kenhTao==='TAI_QUAY'?'TẠI QUẦY':'ONLINE'}</span><h3>${esc(rental.hoTenKhachHang)}</h3><small>${rental.items.map(i=>esc(i.tenSach)+' · '+esc(i.maBanSao)).join('<br>')}</small></div><span class="status-badge">${esc(rentalLabel(rental.trangThai))}</span></div><p>Nhận sách: ${rental.phuongThucNhan==='GIAO_HANG'?'Giao tận nơi':'Tại quầy'} · Tổng thu ${money(Number(rental.tongTienThue)+Number(rental.tongTienCoc)+Number(rental.tongPhiPhat))}</p>${rental.payment?`<p>Thanh toán ${esc(rental.payment.maThanhToan)} · <strong>${esc(rentalLabel(rental.payment.trangThai))}</strong></p>`:''}<div class="actions">${actions.join('')}</div></article>`;
}
async function adminRentalPage(){
 app.innerHTML='<p role="status">Đang tải vận hành cho thuê…</p>';
 try{
  const [books,catalog,rentals,customers,members]=await Promise.all([allPages('sach'),api('rental-catalog'),api('rentals'),allPages('khach-hang'),api('memberships')]);
  const configByBook=new Map(catalog.map(item=>[item.sachId,item]));
  const memberByCust=new Map(members.map(m=>[m.khachHangId,m]));
  app.innerHTML=`<span class="eyebrow">VẬN HÀNH RENTAL</span><h1>Thuê và trả sách</h1><p>Cấu hình đầu sách, quản lý bản sao vật lý và xử lý phiếu online/tại quầy trên cùng một quy trình.</p><div id="admin-rental-message" role="alert" hidden></div><section class="admin-rental-tools"><form id="rental-config-form" class="panel"><h2>Cấu hình sách cho thuê</h2><label>Đầu sách<select name="bookId" required><option value="">Chọn sách</option>${books.filter(b=>b.trangThai).map(b=>`<option value="${b.id}">${esc(b.maSach)} · ${esc(b.tenSach)}${configByBook.has(b.id)?' · đã cấu hình':''}</option>`).join('')}</select></label><div class="form-grid"><label>Giá thuê/ngày<input name="giaThueNgay" type="number" min="0" value="3000" required></label><label>Tiền cọc<input name="tienCoc" type="number" min="0" value="50000" required></label><label>Số ngày mặc định<input name="soNgayMacDinh" type="number" min="1" max="90" value="14" required></label><label>Số lần gia hạn<input name="soLanGiaHan" type="number" min="0" max="10" value="1" required></label><label>Phí trễ/ngày<input name="phiTreNgay" type="number" min="0" value="5000" required></label><label>Trạng thái<select name="choThue"><option value="true">Đang mở thuê</option><option value="false">Tạm dừng</option></select></label></div><button>Lưu cấu hình</button></form><form id="copy-form" class="panel"><h2>Nhập bản sao vật lý</h2><label>Sách đã cấu hình<select name="bookId" required><option value="">Chọn sách</option>${catalog.map(b=>`<option value="${b.sachId}">${esc(b.maSach)} · ${esc(b.tenSach)}</option>`).join('')}</select></label><div class="form-grid"><label>Số lượng<input name="soLuong" type="number" min="1" max="100" value="1" required></label><label>Tình trạng<select name="tinhTrang"><option value="MOI">Mới</option><option value="NHU_MOI">Như mới</option><option value="TOT">Tốt</option><option value="KHA">Khá</option><option value="CU">Cũ</option></select></label></div><label>Ghi chú<textarea name="ghiChu" maxlength="1000"></textarea></label><button>Thêm bản sao</button></form><form id="counter-rental-form" class="panel"><h2>Tạo phiếu tại quầy</h2><label>Khách hàng<select name="customerId" required><option value="">Chọn khách hàng</option>${customers.map(c=>{
    const m=memberByCust.get(c.id);
    const active=m&&m.trangThai==='DANG_HOAT_DONG'&&m.card?.trangThai==='DA_KICH_HOAT';
    const tag=active?' ✓ [Hội viên hợp lệ]':(m?` (Thẻ: ${rentalLabel(m.card?.trangThai||m.trangThai)})`:' (Chưa có thẻ hội viên)');
    return `<option value="${c.id}" ${active?'':'data-not-active="1"'}>${esc(c.hoTen)} · ${esc(c.email||c.soDienThoai||'')}${tag}</option>`;
  }).join('')}</select></label><label>Sách<select name="bookId" required><option value="">Chọn sách</option>${catalog.filter(c=>c.choThue&&c.soBanSaoSanSang>0).map(c=>`<option value="${c.sachId}">${esc(c.tenSach)} · còn ${c.soBanSaoSanSang}</option>`).join('')}</select></label><div class="form-grid"><label>Số lượng<input name="soLuong" type="number" min="1" max="5" value="1" required></label><label>Thanh toán<select name="phuongThucThanhToan"><option value="TIEN_MAT">Tiền mặt tại quầy</option><option value="VIETQR">VietQR</option></select></label></div><label>Ghi chú<textarea name="ghiChu" maxlength="1000">Tạo tại quầy</textarea></label><button>Tạo phiếu thuê</button></form></section><section><div class="section-heading"><div><span class="eyebrow">KHO BẢN SAO</span><h2>Danh mục đang cấu hình</h2></div></div><div class="table-wrap"><table><thead><tr><th>Sách</th><th>Mở thuê</th><th>Giá/ngày</th><th>Cọc</th><th>Kỳ thuê</th><th>Bản sẵn sàng</th></tr></thead><tbody>${catalog.map(c=>`<tr><td>${esc(c.maSach)} · ${esc(c.tenSach)}</td><td>${c.choThue?'Có':'Tạm dừng'}</td><td>${money(c.giaThueNgay)}</td><td>${money(c.tienCoc)}</td><td>${c.soNgayMacDinh} ngày</td><td>${c.soBanSaoSanSang}/${c.tongBanSao}</td></tr>`).join('')||'<tr><td colspan="6">Chưa cấu hình đầu sách cho thuê.</td></tr>'}</tbody></table></div></section><section id="rental-list-section"><div class="section-heading"><div><span class="eyebrow">PHIẾU THUÊ</span><h2>Luồng duyệt, giao và trả</h2></div><button id="mark-overdue" class="secondary">Quét phiếu quá hạn</button></div><div class="order-list">${rentals.length?rentals.map(adminRentalRow).join(''):'<section class="empty-shelf"><h3>Chưa có phiếu thuê</h3></section>'}</div></section>`;
  
  const savedNotice=sessionStorage.getItem('admin_rental_notice');
  if(savedNotice){
    sessionStorage.removeItem('admin_rental_notice');
    adminNotice(savedNotice,false);
    const listSec=document.querySelector('#rental-list-section');
    if(listSec)listSec.scrollIntoView({behavior:'smooth'});
  }

  const returnDialog=document.createElement('dialog');returnDialog.innerHTML='<h2>Kiểm kê khi nhận trả</h2><p>Chọn tình trạng thật cho từng bản. Phí hư hỏng bằng 0 nếu không phát sinh.</p><form id="return-form"><div id="return-items"></div><div class="actions"><button type="submit">Xác nhận nhận trả</button><button type="button" class="secondary" id="return-cancel">Đóng</button></div></form>';app.append(returnDialog);
  const configForm=document.querySelector('#rental-config-form');
  configForm.elements.bookId.onchange=()=>{const c=configByBook.get(Number(configForm.elements.bookId.value));if(!c)return;['giaThueNgay','tienCoc','soNgayMacDinh','soLanGiaHan','phiTreNgay'].forEach(k=>configForm.elements[k].value=c[k]);configForm.elements.choThue.value=String(c.choThue);};
  configForm.onsubmit=async e=>{e.preventDefault();const button=e.currentTarget.querySelector('button'),v=Object.fromEntries(new FormData(e.currentTarget));button.disabled=true;try{await api(`rental-catalog/${v.bookId}`,{method:'PUT',body:JSON.stringify({choThue:v.choThue==='true',giaThueNgay:Number(v.giaThueNgay),tienCoc:Number(v.tienCoc),soNgayMacDinh:Number(v.soNgayMacDinh),soLanGiaHan:Number(v.soLanGiaHan),phiTreNgay:Number(v.phiTreNgay)})});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}};
  document.querySelector('#copy-form').onsubmit=async e=>{e.preventDefault();const button=e.currentTarget.querySelector('button'),v=Object.fromEntries(new FormData(e.currentTarget));button.disabled=true;try{await api(`rental-catalog/${v.bookId}/copies`,{method:'POST',body:JSON.stringify({soLuong:Number(v.soLuong),tinhTrang:v.tinhTrang,ghiChu:v.ghiChu})});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}};
  document.querySelector('#counter-rental-form').onsubmit=async e=>{
    e.preventDefault();
    const form=e.currentTarget;
    const button=form.querySelector('button'),v=Object.fromEntries(new FormData(form));
    const selOpt=form.elements.customerId.selectedOptions[0];
    if(selOpt&&selOpt.dataset.notActive){
      adminNotice('Khách hàng này chưa có thẻ hội viên đã kích hoạt. Vui lòng vào mục "Hội viên" để đăng ký / kích hoạt thẻ trước khi tạo phiếu thuê.', true);
      return;
    }
    button.disabled=true;
    try{
      const res=await api(`rentals/customers/${v.customerId}`,{
        method:'POST',
        body:JSON.stringify({
          clientRequestId:crypto.randomUUID(),
          items:[{sachId:Number(v.bookId),soLuong:Number(v.soLuong)}],
          phuongThucNhan:'TAI_QUAY',
          phuongThucThanhToan:v.phuongThucThanhToan,
          tenNguoiNhan:'',
          soDienThoaiNhan:'',
          diaChiGiao:'',
          ghiChu:v.ghiChu
        })
      });
      sessionStorage.setItem('admin_rental_notice', `✓ Đã tạo thành công phiếu thuê ${res.maPhieuThue}! Bạn có thể bấm "Duyệt phiếu" bên dưới.`);
      location.reload();
    }catch(err){
      adminNotice(err.message,true);
      button.disabled=false;
    }
  };
  document.querySelector('#mark-overdue').onclick=async e=>{e.currentTarget.disabled=true;try{const rows=await api('rentals/overdue');adminNotice(`Đã quét quá hạn: ${rows.length} phiếu cần theo dõi.`);location.reload();}catch(err){adminNotice(err.message,true);e.currentTarget.disabled=false;}};
  document.querySelector('#return-cancel').onclick=()=>returnDialog.close();
  document.querySelectorAll('.reconcile-form').forEach(form=>form.onsubmit=async e=>{e.preventDefault();const button=form.querySelector('button');button.disabled=true;try{await api('payments/' + form.dataset.payment + '/reconcile',{method:'PATCH',body:JSON.stringify({maGiaoDichNhaCungCap:form.elements.reference.value.trim(),ghiChu:'Xác nhận từ trang phiếu thuê'})});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}});
  document.querySelectorAll('[data-admin-rental]').forEach(button=>button.onclick=async()=>{const id=button.dataset.id,action=button.dataset.adminRental;if(action==='return'){const rental=rentals.find(r=>r.id===Number(id)),form=document.querySelector('#return-form');document.querySelector('#return-items').innerHTML=rental.items.map((i,index)=>`<fieldset data-copy="${i.banSaoId}"><legend>${esc(i.tenSach)} · ${esc(i.maBanSao)}</legend><div class="form-grid"><label>Tình trạng khi trả<select name="condition-${index}"><option value="NHU_MOI" ${i.tinhTrangTruoc==='NHU_MOI'?'selected':''}>Như mới</option><option value="TOT" ${i.tinhTrangTruoc==='TOT'||i.tinhTrangTruoc==='MOI'?'selected':''}>Tốt</option><option value="KHA">Khá</option><option value="CU">Cũ</option><option value="HU_HONG">Hư hỏng</option></select></label><label>Phí hư hỏng<input name="damage-${index}" type="number" min="0" value="0" required></label></div></fieldset>`).join('');form.onsubmit=async e=>{e.preventDefault();const submit=form.querySelector('[type=submit]');submit.disabled=true;try{const items=rental.items.map((i,index)=>({banSaoId:i.banSaoId,tinhTrangSau:form.elements[`condition-${index}`].value,phiHuHong:Number(form.elements[`damage-${index}`].value)}));await api(`rentals/${id}/return`,{method:'POST',body:JSON.stringify({items})});location.reload();}catch(err){adminNotice(err.message,true);submit.disabled=false;returnDialog.close();}};returnDialog.showModal();return;}button.disabled=true;try{await api(`rentals/${id}/${action}`,{method:'PATCH'});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}});
 }catch(e){app.innerHTML=`<section class="panel"><h1>Không tải được vận hành rental</h1><p role="alert">${esc(e.message)}</p></section>`;}
}
async function adminMembershipPage(){
 app.innerHTML='<p role="status">Đang tải hội viên…</p>';
 try{
  const [members,customers]=await Promise.all([api('memberships'),allPages('khach-hang')]);
  function memberCard(m){
   const actions=[];
   if(m.trangThai==='CHO_THANH_TOAN'&&m.payment&&['CHO_THANH_TOAN','CHO_DOI_SOAT'].includes(m.payment.trangThai))
    actions.push(`<form class="reconcile-form" data-payment="${m.payment.id}"><input name="reference" required maxlength="100" placeholder="Mã giao dịch / phiếu thu (bắt buộc)"><button>✓ Xác nhận đã thanh toán</button></form>`);
   if(m.trangThai==='DANG_HOAT_DONG'&&m.card.trangThai==='CHO_PHAT_HANH')
    actions.push(`<button data-member-card="${m.id}" data-next="${m.card.phuongThucNhan==='GIAO_HANG'?'DANG_GIAO':'CHO_NHAN'}">${m.card.phuongThucNhan==='GIAO_HANG'?'Bàn giao vận chuyển':'In và chờ nhận thẻ'}</button>`);
   if(m.trangThai==='DANG_HOAT_DONG'&&['CHO_NHAN','DANG_GIAO'].includes(m.card.trangThai))
    actions.push(`<button data-member-card="${m.id}" data-next="DA_KICH_HOAT">Kích hoạt thẻ</button>`);
   return `<article class="panel member-admin-card"><div class="rental-card-head"><div><span class="eyebrow">${esc(m.maHoiVien)}</span><h3>${esc(m.hoTenKhachHang)}</h3><p>${esc(m.card.maThe)} · ${m.card.phuongThucNhan==='GIAO_HANG'?'Giao tận nơi':'Nhận tại quầy'}</p></div><span class="status-badge">${esc(rentalLabel(m.trangThai))}</span></div><p>Thẻ: <strong>${esc(rentalLabel(m.card.trangThai))}</strong>${m.payment?' · Thanh toán '+esc(rentalLabel(m.payment.trangThai)):''}</p><div class="actions">${actions.join('')}</div></article>`;
  }
  app.innerHTML=`<span class="eyebrow">HỘI VIÊN</span><h1>Thẻ thành viên</h1><p>Xác nhận thanh toán để kích hoạt hội viên, sau đó phát hành và kích hoạt thẻ.</p><div id="admin-rental-message" role="alert" hidden></div><form id="counter-membership" class="panel admin-counter-membership"><h2>Đăng ký tại quầy</h2><div class="form-grid"><label>Khách hàng<select name="customerId" required><option value="">Chọn khách hàng</option>${customers.map(c=>`<option value="${c.id}">${esc(c.hoTen)} · ${esc(c.email||c.soDienThoai||'')}</option>`).join('')}</select></label><label>Thanh toán<select name="phuongThucThanhToan"><option value="TIEN_MAT">Tiền mặt</option><option value="VIETQR">VietQR</option></select></label></div><button>Đăng ký thẻ 50.000 đ</button></form><div class="order-list">${members.length?members.map(memberCard).join(''):'<section class="empty-shelf"><h3>Chưa có hội viên</h3></section>'}</div>`;
  document.querySelector('#counter-membership').onsubmit=async e=>{e.preventDefault();const button=e.currentTarget.querySelector('button'),v=Object.fromEntries(new FormData(e.currentTarget));button.disabled=true;try{await api(`memberships/customers/${v.customerId}`,{method:'POST',body:JSON.stringify({phuongThucNhan:'TAI_QUAY',phuongThucThanhToan:v.phuongThucThanhToan,tenNguoiNhan:'',soDienThoai:'',diaChi:''})});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}};
  document.querySelectorAll('.reconcile-form').forEach(form=>form.onsubmit=async e=>{e.preventDefault();const button=form.querySelector('button');button.disabled=true;try{await api(`payments/${form.dataset.payment}/reconcile`,{method:'PATCH',body:JSON.stringify({maGiaoDichNhaCungCap:form.elements.reference.value.trim(),ghiChu:'Xác nhận từ trang hội viên'})});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}});
  document.querySelectorAll('[data-member-card]').forEach(button=>button.onclick=async()=>{button.disabled=true;let tracking='';if(button.dataset.next==='DANG_GIAO')tracking=prompt('Nhập mã vận đơn (có thể để trống nếu chưa có):','')||'';try{await api(`memberships/${button.dataset.memberCard}/card`,{method:'PATCH',body:JSON.stringify({trangThai:button.dataset.next,maVanDon:tracking})});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}});
 }catch(e){app.innerHTML=`<section class="panel"><h1>Không tải được hội viên</h1><p role="alert">${esc(e.message)}</p></section>`;}
}
async function adminPaymentPage(){
 app.innerHTML='<p role="status">Đang tải thanh toán…</p>';
 try{
  const payments=await api('payments');
  app.innerHTML=`<span class="eyebrow">ĐỐI SOÁT THỦ CÔNG</span><h1>Thanh toán rental</h1><p>Chỉ xác nhận sau khi kiểm tra giao dịch thật trên tài khoản cửa hàng. Mã giao dịch không được dùng lại.</p><div id="admin-rental-message" role="alert" hidden></div><div class="table-wrap"><table><thead><tr><th>Mã</th><th>Mục đích</th><th>Phương thức</th><th>Số tiền</th><th>Nội dung</th><th>Trạng thái</th><th>Đối soát</th></tr></thead><tbody>${payments.map(p=>`<tr><td>${esc(p.maThanhToan)}</td><td>${esc(p.mucDich)}</td><td>${esc(p.phuongThuc)}</td><td>${money(p.soTien)}</td><td>${esc(p.noiDungChuyenKhoan)}</td><td>${esc(rentalLabel(p.trangThai))}</td><td>${['CHO_THANH_TOAN','CHO_DOI_SOAT'].includes(p.trangThai)?`<form class="reconcile-form" data-payment="${p.id}"><input name="reference" required maxlength="100" placeholder="Mã giao dịch/phiếu thu"><button>Xác nhận</button></form>`:esc(p.maGiaoDichNhaCungCap||'—')}</td></tr>`).join('')||'<tr><td colspan="7">Chưa có khoản thanh toán.</td></tr>'}</tbody></table></div>`;
  document.querySelectorAll('.reconcile-form').forEach(form=>form.onsubmit=async e=>{e.preventDefault();const button=form.querySelector('button');button.disabled=true;try{await api(`payments/${form.dataset.payment}/reconcile`,{method:'PATCH',body:JSON.stringify({maGiaoDichNhaCungCap:form.elements.reference.value.trim(),ghiChu:'Đối soát từ trang quản trị'})});location.reload();}catch(err){adminNotice(err.message,true);button.disabled=false;}});
 }catch(e){app.innerHTML=`<section class="panel"><h1>Không tải được thanh toán</h1><p role="alert">${esc(e.message)}</p></section>`;}
}
