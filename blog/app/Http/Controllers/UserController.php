<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/18
 * Time: 18:22
 */
namespace App\Http\Controllers;

use App\Client;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

date_default_timezone_set('Asia/Shanghai');
class UserController extends Controller {

    public function login(Request $request) {

//        session()->flush();
//        session()->put('key','value');
//        var_dump(session()->exists('key'));
        $fields = [
            'email' => 'required|min:2|max:20|email',
            'password' => 'required|min:2|max:20',
        ];
        $validator = Validator::make($request->input(), $fields, [
            'required' => ':attribute is empty ',
            'min' => ':attribute size is too short',
            'max' => ' :attribute size is too long'
        ]);
        $errors = $validator->errors();

        $msg = [];
        foreach ($fields as $k => $v) {
            $error = $errors->get($k);
            if (empty($error)) {
                continue;
            }
            $msg[] = $error;
        }
        if(empty($msg)){
            $tmp = DB::table('client')->where('email','=',$request->email)->exists();
            if($tmp) {
                if ($request->session()->has($request->email)) {
                    $email = DB::table('client')->where('email', $request->email)->value('email');
                    $created_at = DB::table('client')->where('email', $request->email)->value('created_at');
                    $updated_at = DB::table('client')->where('email', $request->email)->value('updated_at');
                    return response(['email' => $email, 'created_at' => date('Y-m-d H:i:s', $created_at), 'updated_at' => date('Y-m-d H:i:s', $updated_at)]);


                } else {
                    $tmp = DB::table('client')->select('password')->where('email', '=', $request->email)->first()->password;
                    if (Hash::check($request->password, $tmp)) {
                        $request->session()->push($request->email,'email');
                        return response(['status' => true, 'msg' => 'login success']);
                    } else
                        return response(['status' => false, 'msg' => 'error password']);
                }
            }else{
                return response(['status'=>false,'msg'=>'the email is not exist']);
            }
        }else{
            return response(['status'=>false, 'msg'=>$msg]);
        }
    }

    public function register(Request $request){
        $fields = [
            'email' => 'required|min:2|max:20|email',
            'password' => 'required|min:2|max:20',
        ];
       $validator = Validator::make($request->input(), $fields, [
            'required' => ' :attribute is empty ',
            'min' => ':attribute size is too short',
           'max' => ' :attribute size is too long'
        ]);
       $errors = $validator->errors();

       $msg = [];
        foreach ($fields as $k => $v) {
            $error = $errors->get($k);
            if (empty($error)) {
                continue;
            }
            $msg[] = $error;
        }
        if(empty($msg)){

            $tmp1 = DB::table('client')->where('email','=',$request->email)->exists();
            $tmp2 = DB::table('client')->where('tel','=',$request->tel)->exists();
            if($tmp1 || $tmp2){
                return response(['status'=>false,'msg'=>'user exist']);
            }else{
                $name = $request->input('name');
                $password = $request->input('password');
                $email = $request->input('email');
                $tel = $request->input("tel");
//
                $hashed =  Hash::make($password);
                $client = new Client();
                $client->name = $name;
                $client->password = $hashed;
                $client->email = $email;
                $client->tel = $tel;
//                dd($client);
                $client->save();
//
                return response(['status'=>true,'msg'=>'register success']);
            }
        }else{
            return response(['status'=>false, 'msg'=>$msg]);
        }

    }
}