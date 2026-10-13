# 推送到 GitHub —— 一条命令搞定
#
# 双击运行，或者在 PowerShell 里执行：
#     powershell -ExecutionPolicy Bypass -File .\push-to-github.ps1
#
# 前提：本机已能 SSH 到 GitHub（已用 key id_ed25519_github 验证通过），
#      且 github.com 上已经建好仓库 ZGY210/java-learning-notes。

$ErrorActionPreference = 'Continue'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

Set-Location $PSScriptRoot

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "  推送 java-learning-notes 到 GitHub" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# ---------- 第 1 步：确认本地有提交 ----------
Write-Host "[1/4] 检查本地提交 ..." -ForegroundColor Yellow
$log = git log --oneline -1 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "  没有找到任何提交，先确认你在正确的目录里。" -ForegroundColor Red
    exit 1
}
Write-Host "  $log"
Write-Host ""

# ---------- 第 2 步：确认 remote 配好 ----------
Write-Host "[2/4] 检查远程地址 ..." -ForegroundColor Yellow
$remote = git config --get remote.origin.url 2>&1
if (-not $remote) {
    Write-Host "  尚未配置 remote，正在添加 ..." -ForegroundColor Yellow
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
    Write-Host "  SSH 认证失败，输出如下：" -ForegroundColor Red
    Write-Host $sshOut
    Write-Host ""
    Write-Host "  排查方向：" -ForegroundColor Yellow
    Write-Host "  1) 确认公钥已加到 GitHub：https://github.com/settings/keys"
    Write-Host "     本机公钥内容（复制这一整行）："
    Get-Content "$env:USERPROFILE\.ssh\id_ed25519_github.pub" -ErrorAction SilentlyContinue
    Write-Host "  2) 确认 ~/.ssh/config 里有走 443 端口的配置（本机已有）"
    exit 1
}
Write-Host ""

# ---------- 第 4 步：推送 ----------
Write-Host "[4/4] 推送 main 分支 ..." -ForegroundColor Yellow
git push -u origin main 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "  推送失败。最常见的两种原因：" -ForegroundColor Red
    Write-Host "  A. 远程仓库还不存在 -> 先去建（不要勾 README / .gitignore / license）："
    Write-Host "     https://github.com/new?name=java-learning-notes&visibility=public"
    Write-Host "  B. 远程已是 master 分支而我们推 main -> 在 GitHub 仓库 Settings 里"
    Write-Host "     把默认分支改成 main，或者执行： git push -u origin main:master"
    exit 1
}

Write-Host ""
Write-Host "==============================================" -ForegroundColor Green
Write-Host "  推送成功" -ForegroundColor Green
Write-Host "  仓库地址：https://github.com/ZGY210/java-learning-notes" -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
Write-Host ""
Write-Host "以后每天收工时的固定三连：" -ForegroundColor Cyan
Write-Host "    .\verify.cmd"
Write-Host "    git add -A"
Write-Host "    git commit -m `"feat: 今天做了什么`""
Write-Host "    git push"
