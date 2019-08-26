package com.northmeter.northmetersmart.control;

/**
 * Created by dyd on 2019/8/9.
 */
public class AuxHW {

    //指令模板：zl="开，模式，24度，风速，上下，左右"

    public String getControlAux(String zl){
        String hwm = null;
        String[] zllist = zl.split(",");
        int temputers=Integer.parseInt(zllist[2]);
        if(zllist[0].equals("关")){
            hwm = "0204027D020902EF0601012823461268C387E000A000200000000005EF";
        }else{
            switch(zllist[1]){
                case "制冷":
                    hwm = getColdStrFromTem(temputers);
                    break;
                case "制热":
                    hwm = getHotStrFromTem(temputers);
                    break;
                default:
                    hwm = getColdStrFromTem(temputers);
                    break;
            }
        }
        return hwm;
    }

    /**
     * 根据温度获取制冷红外码*/
    private String getColdStrFromTem(int temp){
        String coldHwm = null;
        switch (temp){
            case 20:
                coldHwm = "06027A020B02EC0601012623461268C367E000A000200000200000EA";
                break;
            case 21:
                coldHwm = "0E0276021002E70601016A23151268C36FE000A000200000200000F2";
                break;
            case 22:
                coldHwm = "0E0275021302E30601014C23481268C377E000A000200000200000FA";
                break;
            case 23:
                coldHwm = "0E0275021002E80601014E230F1268C37FE000A00020000020000002";
                break;
            case 24:
                coldHwm = "0F0274021102E40601014E23141268C387E000A0002000002000000A";
                break;
            case 25:
                coldHwm = "120273021402E00601014C23491268C38FE000A00020000020000012";
                break;
            case 26:
                coldHwm = "110273021302E60601014E23481268C397E000A0002000002000001A";
                break;
            case 27:
                coldHwm = "0E0276020D02E70601016D23491268C39FE000A00020000020000022";
                break;
            case 28:
                coldHwm = "120272021202E70601016F23131268C3A7E000A0002000002000002A";
                break;
                default:
                    coldHwm = "06027A020B02EC0601012623461268C367E000A000200000200000EA";
                    break;
        }
        return "01"+coldHwm;
    }


    /**
     * 根据温度获取制热红外码*/
    private String getHotStrFromTem(int temp){
        String coldHwm = null;
        switch (temp){
            case 20:
                coldHwm = "FF0183020A02EC0601012A23461268C367E000A0008000003000005A";
                break;
            case 21:
                coldHwm = "1C0267021502DB0601018923CE1168C36FE000A00080000030000062";
                break;
            case 22:
                coldHwm = "24025F022C02C3060101A623CE1168C377E000A0008000003000006A";
                break;
            case 23:
                coldHwm = "1B0268023A02BA0601018C23CB1168C37FE000A00080000030000072";
                break;
            case 24:
                coldHwm = "230260022E02C80601018B239F1168C387E000A0008000003000007A";
                break;
            case 25:
                coldHwm = "1D0266023002C5060101A123CF1168C38FE000A00080000030000082";
                break;
            case 26:
                coldHwm = "220263022E02C40601018A23CE1168C397E000A0008000003000008A";
                break;
            case 27:
                coldHwm = "210261023202C3060101A523CE1168C39FE000A00080000030000092";
                break;
            case 28:
                coldHwm = "3C02440256029F0601012A23481268C3A7E000A0008000003000009A";
                break;
            case 29:
                coldHwm = "190268021F02DB0601018923D21168C3AFE000A000800000300000A2";
                break;
            case 30:
                coldHwm = "2A0259024A02AC060101AB23CE1168C3B7E000A000800000300000AA";
                break;
                default:
                    coldHwm = "230260022E02C80601018B239F1168C387E000A0008000003000007A";
                    break;
        }
        return "01"+coldHwm;
    }
}


/**
 * //开机20度制冷自动风速
 * 06027A020B02EC0601012623461268C367E000A000200000200000EA //20
 * 0E0276021002E70601016A23151268C36FE000A000200000200000F2
 * 0E0275021302E30601014C23481268C377E000A000200000200000FA
 * 0E0275021002E80601014E230F1268C37FE000A00020000020000002
 * 0F0274021102E40601014E23141268C387E000A0002000002000000A
 * 120273021402E00601014C23491268C38FE000A00020000020000012
 * 110273021302E60601014E23481268C397E000A0002000002000001A
 * 0E0276020D02E70601016D23491268C39FE000A00020000020000022 //27
 * //开机28度制冷自动风速
 * 120272021202E70601016F23131268C3A7E000A0002000002000002A
 *
 *
 * //开机20度制热自动风速
 * FF0183020A02EC0601012A23461268C367E000A0008000003000005A  //20
 * 1C0267021502DB0601018923CE1168C36FE000A00080000030000062
 * 24025F022C02C3060101A623CE1168C377E000A0008000003000006A
 * 1B0268023A02BA0601018C23CB1168C37FE000A00080000030000072
 *
 * 230260022E02C80601018B239F1168C387E000A0008000003000007A
 * 1D0266023002C5060101A123CF1168C38FE000A00080000030000082
 * 220263022E02C40601018A23CE1168C397E000A0008000003000008A
 * 210261023202C3060101A523CE1168C39FE000A00080000030000092
 * 3C02440256029F0601012A23481268C3A7E000A0008000003000009A
 * 190268021F02DB0601018923D21168C3AFE000A000800000300000A2  //29
 * //开机30度制热自动风速
 * 2A0259024A02AC060101AB23CE1168C3B7E000A000800000300000AA
 *
 *
 * //关机 制冷 24 自动风速
 * 04027D020902EF0601012823461268C387E000A000200000000005EF*/