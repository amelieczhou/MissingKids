<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/18
 * Time: 18:22
 */
namespace App\Http\Controllers;

use App\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

date_default_timezone_set('Asia/Shanghai');
class UserController extends Controller {

    public function login(Request $request) {
        $login_state = $this->getLoginState();
        if($login_state){
            return $this->simpleJsonError(200,'已登录');
        }

        $email = $request->input('email');

//        取出用户信息
        $ad_user = DB::table('user')
            ->where([
                ['email',$email],
            ])
            ->first();

        if(empty($ad_user) || !Hash::check($request->password,$ad_user->password)){
            return $this->simpleJsonError('账号或密码错误！');
        }

        if($ad_user->state == 0){
            return $this->simpleJsonError('账号已经被禁用，请联系系统管理员！');
        }
        session()->put([
            'user_id' => $ad_user->id,
        ]);
        return $this->simpleJsonSuccess('登陆成功');
    }



    public function register(Request $request){

        $tmp1 = DB::table('user')
            ->where('email','=',$request->email)
            ->exists();

        $tmp2 = DB::table('user')
            ->where('tel','=',$request->tel)
            ->exists();


        if($tmp1 || $tmp2){
            return $this->simpleJsonError('账号已存在');
        }else {
            $name = $request->input('name');
            $password = $request->input('password');
            $email = $request->input('email');
            $tel = $request->input("tel");

            $hashed = Hash::make($password);
            $fields = [
                'name' => $name,
                'password' => $hashed,
                'email' => $email,
                'tel' => $tel,
                'created_at' => time(),
                'updated_at' => time()
            ];

            $res = DB::table('user')
                ->insert($fields);

            if(!$res){
                return $this->simpleJsonError('注册失败，请重试');
            }


            return $this->simpleJsonSuccess('注册成功');
        }
    }

    public function getPosition(Request $request){

        $id = session('user_id');
//            $id = 67;
        if(empty($id)){
            return $this->simpleJsonError('please login first');
        }

        $time = $request->input('time');
        $latitude = $request->input('latitude');
        $longitude = $request->input('longitude');
//        if(empty($time)){
//            return $this->simpleJsonError('empty input');
//        }

        $result = DB::table('user')
            ->where('id',$id)
            ->update([
                'time' => $time,
                'latitude' => $latitude,
                'longitude' => $longitude,
            ]);

        if(!$result){
            return $this->simpleJsonError('insert fail');
        }

        return $this->simpleJsonSuccess('insert success');

    }
}