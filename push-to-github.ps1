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
#  中文会变乱码并破坏语法，症状就是命令被打印出来却不执行。
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
$log = git log --oneline -1 2>&1
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
$sshOut = ssh -T -o ConnectTimeout=10 git@github.com 2>&1 | Out-String
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
git push -u origin main 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "  推送失败。最常见的两种原因：" -ForegroundColor Red
    Write-Host "  A. 远程仓库还不存在。先去新建：https://github.com/new"
    Write-Host "     仓库名填 java-learning-notes，可见性选 Public，"
    Write-Host "     下面三个勾选项（README / .gitignore / license）一个都不要勾。"
    Write-Host "  B. 远程已存在且默认分支是 master（我们本地是 main）："
    Write-Host "     解法 1（推荐）：进 GitHub 仓库 Settings -> Branches，把默认分支改成 main"
    Write-Host "     解法 2：直接推成 master： git push -u origin main:master"
    exit 1
}

Write-Host ""
Write-Host "==============================================" -ForegroundColor Green
Write-Host "  推送成功" -ForegroundColor Green
Write-Host "  仓库地址：https://github.com/ZGY210/java-learning-notes" -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
Write-Host ""
Write-Host "以后每天收工时的固定动作：" -ForegroundColor Cyan
Write-Host "    .\verify.cmd"
Write-Host "    git add -A"
$commitDemo = '    git commit -m "feat: 今天做了什么"'
Write-Host $commitDemo
Write-Host "    git push"
