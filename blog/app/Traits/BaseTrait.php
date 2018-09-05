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
}