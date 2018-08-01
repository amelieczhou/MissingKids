<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/
Route::get('/', function () {
    return view('welcome');
});

Auth::routes();
Route::get('/home', 'HomeController@index')->name('home');

Route::any('student/index','StudentController@index');
Route::any('student/create','StudentController@create');
Route::any('student/save','StudentController@save');
Route::any('student/update/{id}','StudentController@update');
Route::any('student/detail/{id}','StudentController@detail');
Route::any('student/delete/{id}','StudentController@delete');

Route::any('cache1','TestController@cache1');
Route::any('queue','TestController@queue');







