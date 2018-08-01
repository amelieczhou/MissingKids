<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/25
 * Time: 17:26
 */
namespace App;

use Illuminate\Database\Eloquent\Model;

class Test extends Model{
    protected $table = 'student';

    protected $fillable = ['name','age'];
    public function getDateFormat(){
        return time();
    }

//    protected function asDateTime($value)
//    {
//        return $value;
//    }

}