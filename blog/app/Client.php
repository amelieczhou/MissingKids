<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/18
 * Time: 19:06
 */

namespace App;

use Illuminate\Database\Eloquent\Model;

class Client extends Model{
    public $table = 'client';


    public $timestamps = true;

    public function getDateFormat(){
        return time();
    }

    protected function asDateTime($value)
    {
        return $value;
    }
}