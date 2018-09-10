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


    //获取用户实时位置信息
    Route::post('position','UserController@getPosition');

    //丢失儿童信息
    Route::post('create','KidsController@create');
    Route::post('addDescAndPic','KidsController@addDescAndPic');
    Route::post('addPosition','KidsController@addPosition');
    Route::post('edit','KidsController@edit');
    Route::get('list','KidsController@list');
    Route::post('del','KidsController@del');
    Route::post('getOne','KidsController@getOne');
    Route::get('getAllPosition','KidsController@getAllPosition');
    Route::get('getAllInfo','KidsController@getAllInfo');
    Route::post('uploadPic','KidsController@uploadPic');
    Route::post('uploadDes','KidsController@uploadDes');
    Route::get('getAllPic','KidsController@getAllPic');
    Route::post('news','KidsController@News');

//    Route::get('imgsys/{one?}/{two?}/{three?}/{four?}/{five?}/{six?}/{seven?}/{eight?}/{nine?}',function(){
//        \App\Util\ImageRoute::imageStorageRoute();
//    });



});



