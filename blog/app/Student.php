<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/18
 * Time: 16:04
 */

namespace App;

use Illuminate\Database\Eloquent\Model;

class Student extends Model
{

    const SEX_UN = 10;
    const SEX_BOY = 20;
    const SEX_GIRL = 30;
    public $table = 'student';

    protected $primaryKey = 'id';

    public $timestamps = true;

    protected $fillable = ['name', 'age', 'sex'];

    public function getDateFormat()
    {
        return time();
    }
//
    public function asDateTime($val)
    {
        return $val;
    }

    public function fromDateTime($value)
    {
        return empty($value) ? $value :$this->asDateTime();
    }


    public function sex1($ind = null)
    {
        $arr = [
            self::SEX_UN => '未知',
            self::SEX_BOY => '男',
            self::SEX_GIRL => '女'
        ];
        if ($ind !== null) {
            return array_key_exists($ind, $arr) ? $arr[$ind] : $arr[self::SEX_UN];
        }
        return $arr;
    }
}