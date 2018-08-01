<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/16
 * Time: 18:18
 */

namespace App\Http\Middleware;

class Activity{
    public function handle($request,\Closure $next){
//        if(time() < strtotime('2018-07-26')){
//            return redirect('session1');
//        }
        $response = $next($request);
        var_dump($response);
    }
}