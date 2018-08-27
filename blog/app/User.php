<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/18
 * Time: 19:06
 */

namespace App;

use Illuminate\Database\Eloquent\Model;

class User extends Model{
    public $table = 'user';


    public $timestamps = true;

    public function getDateFormat(){
        return time();
    }

    protected $fillable = [
        'name', 'email', 'password','tel','state',
    ];

    protected function asDateTime($value)
    {
        return $value;
    }
}