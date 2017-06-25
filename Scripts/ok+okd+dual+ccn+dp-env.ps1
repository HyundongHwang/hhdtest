sal dp "C:\DangolPlus\Update.exe"
sal ok "C:\project\161010_CoconutClient\bin\CoconutBridge\Debug\OkposMock.exe"
sal okd "C:\project\161010_CoconutClient\bin\CoconutBridge\Debug\OkdaemonMock.exe"
sal coconut "C:\project\161010_CoconutClient\bin\Coconut\Debug\Coconut.exe"

function hhdkilldpokokdcoconut
{
    hhdkillwithchild @("posagent", "okpos", "okdaemon", "coconut")   
}

function hhdstartdpokokdcoconut
{
    dp
    ok
    coconut
}