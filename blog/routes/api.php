<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

//Route::middleware('auth:api')->get('/user', function (Request $request) {
//    return $request->user();
//});





Route::group(['middleware'=>['web']], function (){
    //登陆相关接口
    Route::post('login', 'UserController@login');
    Route::post('register','UserController@register');

    //创建表单接口
    Route::post('create','KidsController@create');
    Route::post('addDescAndPic','KidsController@addDescAndPic');
    Route::post('addPosition','KidsController@addPosition');
    Route::post('edit','KidsController@edit');
    Route::get('list','KidsController@list');
    Route::post('del','KidsController@del');
    Route::post('getOne','KidsController@getOne');

});



