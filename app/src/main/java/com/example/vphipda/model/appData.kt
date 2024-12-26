package com.example.vphipda.model


class appData() {

    companion object {
         var BaseURL:String = "http://59.29.54.54:8070/api/"
         var AppURL:String = "http://59.29.54.54:8070/media/"
//         var BaseURL:String = "http://222.96.199.9/api/"
//         var AppURL:String = "http://222.96.199.9/media/"
//         var BaseURL:String = "http://192.168.0.11:8000/api/"
//         var AppURL:String = "http://192.168.0.11:8000/media/"

         var ActivF:String = ""
         var PackingOrderCode:String = "" // 해외 출고 수주 번호
         var AppVersion:String = "pda1.0.14"
         var CaseNum = "" // 대포장 번호
         var PmsNum = "" // pms 번호
         var DataNoWo = ""
         var SampleType = ""
         var SampleNoio = ""
         var SampleText = ""

         var CDVPLANT = ""
         var AlertS:String = "전송 성공"
         var AlertE:String = "전송 실패"
         var AlertF:String = "서버와 연결할 수 없습니다."

         //해외 출고
         var ScanCodeState:Int = 0
         var ScanCodeRack:String = ""
         var ScanCodeItem:String = ""
         var ScanCodeQt:String = ""

         // 기기 명
         var An_ID:String = ""
    }
}







