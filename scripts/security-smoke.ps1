$base='http://localhost:8094'
function Check($condition,$name){if(!$condition){throw $name};Write-Output "PASS: $name"}
$guest=New-Object Microsoft.PowerShell.Commands.WebRequestSession
$r=Invoke-WebRequest "$base/api/khach-hang" -WebSession $guest -SkipHttpErrorCheck;Check ($r.StatusCode -eq 401) 'Guest cannot read customers'
$c=Invoke-RestMethod "$base/api/auth/csrf" -WebSession $guest
$r=Invoke-WebRequest "$base/api/auth/login" -WebSession $guest -Method Post -Headers @{$c.headerName=$c.token} -Body @{username='admin';password='wrongpass'} -SkipHttpErrorCheck;Check ($r.StatusCode -eq 401) 'Wrong password rejected'
$c=Invoke-RestMethod "$base/api/auth/csrf" -WebSession $guest
$r=Invoke-RestMethod "$base/api/auth/login" -WebSession $guest -Method Post -Headers @{$c.headerName=$c.token} -Body @{username='bandoc';password='BanDoc@123456'};Check ($r.role -eq 'KHACH_HANG') 'Reader login'
$r=Invoke-WebRequest "$base/api/khach-hang" -WebSession $guest -SkipHttpErrorCheck;Check ($r.StatusCode -eq 403) 'Reader cannot read customer management'
$c=Invoke-RestMethod "$base/api/auth/csrf" -WebSession $guest
$r=Invoke-WebRequest "$base/api/sach" -WebSession $guest -Method Post -Headers @{$c.headerName=$c.token} -ContentType 'application/json' -Body '{}' -SkipHttpErrorCheck;Check ($r.StatusCode -eq 403) 'Reader cannot create books'
$r=Invoke-WebRequest "$base/chi-tiet.html?id=1" -WebSession $guest;Check ($r.StatusCode -eq 200) 'Reader can open detail'
$r=Invoke-WebRequest "$base/api/auth/logout" -WebSession $guest -Method Post -Headers @{$c.headerName=$c.token};Check ($r.StatusCode -eq 204) 'Logout'
$r=Invoke-RestMethod "$base/api/auth/me" -WebSession $guest;Check ($r.role -eq 'GUEST') 'Session removed'
$c=Invoke-RestMethod "$base/api/auth/csrf" -WebSession $guest
$r=Invoke-RestMethod "$base/api/auth/login" -WebSession $guest -Method Post -Headers @{$c.headerName=$c.token} -Body @{username='admin';password='Admin@123456'};Check ($r.role -eq 'ADMIN') 'Admin login'
$r=Invoke-WebRequest "$base/api/khach-hang" -WebSession $guest;Check ($r.StatusCode -eq 200) 'Admin can read customers'
$r=Invoke-WebRequest "$base/api/sach" -WebSession $guest -Method Post -ContentType 'application/json' -Body '{}' -SkipHttpErrorCheck;Check ($r.StatusCode -eq 403) 'Missing CSRF rejected'
