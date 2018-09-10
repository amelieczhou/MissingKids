<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/8/19
 * Time: 14:57
 */

namespace App\Traits;

trait BaseTrait{
    public function simpleJsonError($data = '',$code=''){
        return array('success' =>FALSE, 'code'=>$code, 'data'=>$data);
    }

    public function simpleJsonSuccess($data=array(), $code=200){
        return array('success'=>TRUE, 'code'=>$code, 'data'=>$data);
    }

    public function getLoginState()
    {
        if(session()->has('user_id')){
            return true;
//            $old_ip = session()->get('admin:ip');
//            if(!empty($old_ip) && $old_ip == $ip){
//                return true;
//            }
        }
        return false;
    }

    public function isId($id)
    {
        if (empty($id) || !is_numeric($id) || $id < 0 || !is_bool((strpos($id, '.')))) {
            return false;
        }
        return true;
    }

    public function GetDistance($lat1, $lng1, $lat2, $lng2, $len_type = 1, $decimal = 2)
    {
        $radLat1 = $lat1 * PI / 180.0;
        $radLat2 = $lat2 * PI / 180.0;
        $a = $radLat1 - $radLat2;
        $b = ($lng1 * PI / 180.0) - ($lng2 * PI / 180.0);
        $s = 2 * asin(sqrt(pow(sin($a/2),2) + cos($radLat1) * cos($radLat2) * pow(sin($b/2),2)));
        $s = $s * EARTH_RADIUS;
        $s = round($s * 1000);
        if ($len_type > 1)
        {
            $s /= 1000;
        }
        return round($s, $decimal);
    }
}