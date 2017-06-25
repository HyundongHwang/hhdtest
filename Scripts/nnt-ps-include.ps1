sal open "C:\Windows\SysWOW64\explorer.exe"
sal vs2015 "C:\Program Files (x86)\Microsoft Visual Studio 14.0\Common7\IDE\devenv.com"
sal makensis "$PSScriptRoot\..\installer\NSIS\Unicode\makensis.exe"
sal signtool "C:\Program Files (x86)\Windows Kits\10\bin\x64\signtool.exe"
sal nuget "$PSScriptRoot\NuGet.exe"



<#
.SYNOPSIS
.EXAMPLE
#>
function hhdmoduleinstallimport
{
    [CmdletBinding()]
    param
    (
        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [System.String]
        $MODULE_NAME
    )

    if((Get-InstalledModule -name $MODULE_NAME).count -eq 0)
    {
        write "install $MODULE_NAME ..."
        Install-Package $MODULE_NAME -Force -AllowClobber
    }
    else 
    {
        write "$MODULE_NAME already installed !!!"
    }

    write "import $MODULE_NAME ..."
    Import-Module $MODULE_NAME -Force
}






$envPath = cat Env:\Path

if (! $envPath -like "*C:\Program Files\nodejs*") 
{
    Write-Error "add nodejs to Env:\Path !!!"
}

if (! $envPath -like "*git*") 
{
    Write-Error "add git to Env:\Path !!!"
}