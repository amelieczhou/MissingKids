<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/25
 * Time: 17:02
 */
namespace App\Http\Controllers;

date_default_timezone_set('Asia/Shanghai');

use App\Jobs\SendEmail;
use App\Jobs\SendMail;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Storage;
use Mail;

class TestController extends Controller
{
    public function queue(){
        return view('adsfa');
    }
}