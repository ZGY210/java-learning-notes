# ============================================================
#  push-to-github.ps1
#
#  用法（任选其一）：
#     1) 双击本文件
#     2) 在 PowerShell 里执行：
#        powershell -ExecutionPolicy Bypass -File .\push-to-github.ps1
#
#  前提：
#     - 本机已能 SSH 到 GitHub（key: id_ed25519_github，走 443 端口）
#     - github.com 上已建好仓库 ZGY210/java-learning-notes
#
#  重要：本文件必须保存为「UTF-8 带 BOM」。
#  Windows PowerShell 5.1 在没有 BOM 时会按系统 ANSI(GBK) 解码 .ps1，
#  中文会变乱码并破坏引号配对，症状是命令被原样打印出来却不执行。
# ============================================================

$ErrorActionPreference = 'Continue'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Set-Location $PSScriptRoot

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "  推送 java-learning-notes 到 GitHub" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# ---------- 第 1 步：确认本地有提交 ----------
Write-Host "[1/4] 检查本地提交 ..." -ForegroundColor Yellow
$log = (cmd /c "git log --oneline -1 2>&1") -join "`n"
if ($LASTEXITCODE -ne 0) {
    Write-Host "  找不到任何提交。先确认你在正确的目录里。" -ForegroundColor Red
    exit 1
}
Write-Host "  $log"
Write-Host ""

# ---------- 第 2 步：确认 remote 配好 ----------
Write-Host "[2/4] 检查远程地址 ..." -ForegroundColor Yellow
$remote = git config --get remote.origin.url 2>&1
if (-not $remote) {
    Write-Host "  尚未配置 remote，正在自动添加 ..." -ForegroundColor Yellow
    git remote add origin git@github.com:ZGY210/java-learning-notes.git
    $remote = git config --get remote.origin.url
}
Write-Host "  origin = $remote"
Write-Host ""

# ---------- 第 3 步：先测试 SSH 能不能连上 ----------
Write-Host "[3/4] 测试与 GitHub 的 SSH 连接 ..." -ForegroundColor Yellow
$sshOut = (cmd /c "ssh -T -o ConnectTimeout=10 git@github.com 2>&1") -join "`n"
if ($sshOut -match 'successfully authenticated') {
    Write-Host "  SSH 认证成功" -ForegroundColor Green
    if ($sshOut -match 'Hi (\S+?)!') {
        Write-Host "  当前身份：$($Matches[1])"
    }
} else {
    Write-Host "  SSH 认证失败，原始输出如下：" -ForegroundColor Red
    Write-Host $sshOut
    Write-Host ""
    Write-Host "  排查方向：" -ForegroundColor Yellow
    Write-Host "  1) 确认公钥已加到 https://github.com/settings/keys"
    Write-Host "     本机公钥内容（复制这一整行）："
    Get-Content "$env:USERPROFILE\.ssh\id_ed25519_github.pub" -ErrorAction SilentlyContinue
    Write-Host "  2) 确认 ~/.ssh/config 里配置了走 443 端口（本机已有）"
    exit 1
}
Write-Host ""

# ---------- 第 4 步：推送 ----------
Write-Host "[4/4] 推送 main 分支 ..." -ForegroundColor Yellow
# 用 cmd /c 捕获输出：git 把进度信息写到 stderr，若用 PowerShell 的 2>&1 管道，
# 即便推送成功也会渲染成一片红色 NativeCommandError，看起来像失败。
$pushOut  = (cmd /c "git push -u origin main 2>&1") -join "`n"
$pushCode = $LASTEXITCODE
Write-Host $pushOut

if ($pushCode -eq 0) {
    Write-Host "==============================================" -ForegroundColor Green
    Write-Host "  推送成功" -ForegroundColor Green
    Write-Host "  仓库地址：https://github.com/ZGY210/java-learning-notes" -ForegroundColor Green
    Write-Host "==============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "以后每天收工时的固定动作：" -ForegroundColor Cyan
    Write-Host "    .\verify.cmd"
    Write-Host "    git add -A"
    Write-Host '    git commit -m "feat: 今天做了什么"'
    Write-Host "    git push"
    exit 0
}

# 失败时按真实错误分类给出解法（旧版只会一律说「仓库不存在」，会误导）
Write-Host "==============================================" -ForegroundColor Red
Write-Host "  推送失败，请对照下面的原因处理" -ForegroundColor Red
Write-Host "==============================================" -ForegroundColor Red
Write-Host ""

# 注意：git 在多种失败下都会打 "Could not read from remote repository"，这是通病提示，
# 不能用来判断原因。所以先匹配最具体的特征串，原因看输出里的第一行。
if ($pushOut -match 'couldn''t create signal pipe|Win32 error 5') {
    Write-Host "原因：当前终端跑在受限沙箱里，git 内部的 sh.exe 无法创建管道。" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "解法：换一个普通的 PowerShell 窗口重跑本脚本。" -ForegroundColor Cyan
    Write-Host "     （这是沙箱环境特有的限制，正常终端不会出现。）"
}
elseif ($pushOut -match 'Permission denied \(publickey\)|Permission denied \(') {
    Write-Host "原因：SSH 密钥认证被拒。" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "解法：" -ForegroundColor Cyan
    Write-Host "    1) 浏览器打开 https://github.com/settings/keys 确认公钥已添加，"
    Write-Host "       本机公钥（复制下面这一整行）："
    Get-Content "$env:USERPROFILE\.ssh\id_ed25519_github.pub" -ErrorAction SilentlyContinue
    Write-Host "    2) 确认 ~/.ssh/config 里 github.com 走的是 443 端口。"
}
elseif ($pushOut -match 'non-fast-forward|fetch first|Updates were rejected|\[rejected\]') {
    Write-Host "原因：远程仓库里已经有提交（例如建仓库时勾了 Add a README），" -ForegroundColor Yellow
    Write-Host "      本地和远程是两条不相干的历史，Git 拒绝了这次推送。" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "解法（推荐，保留远程那个 README）：" -ForegroundColor Cyan
    Write-Host "    git pull --rebase origin main"
    Write-Host "    git push -u origin main"
    Write-Host ""
    Write-Host "解法（远程那个 README 不要了，直接覆盖）：" -ForegroundColor Cyan
    Write-Host "    git push -u origin main --force"
    Write-Host "    仓库是你自己的、刚建的，force 覆盖没有风险。"
}
elseif ($pushOut -match 'Repository not found|does not appear to be a git repository') {
    Write-Host "原因：GitHub 上找不到这个仓库，或者地址写错了。" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "解法：" -ForegroundColor Cyan
    Write-Host "    1) 浏览器打开 https://github.com/ZGY210/java-learning-notes"
    Write-Host "       如果 404 -> 去 https://github.com/new 新建，"
    Write-Host "       名字填 java-learning-notes，选 Public，三个勾选项都不要勾。"
    Write-Host "    2) 新建后回到这里重新运行本脚本。"
}
else {
    Write-Host "没有匹配到已知的错误模式，完整输出见上方。" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "把上面这段输出原样贴给我，我帮你定位。" -ForegroundColor Cyan
}

exit 1
