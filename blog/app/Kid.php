<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Kid extends Model
{
    public $table = 'missingkids';


    public $timestamps = true;
    protected $fillable = [
        'name', 'age','sex','parent','parent_tel','missing_time',
    ];

}
