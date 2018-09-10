<?php


namespace App\Http\Controllers;
define('EARTH_RADIUS', 6378.137);//地球半径
define('PI', 3.1415926);

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use Tuisong;

class KidsController extends Controller
{


    /**
     * @param Request $request
     * @return array
     * 新增丢失儿童
     */
    public function create(Request $request){
        $name = $request->name;
        $age = $request->age;
        $sex = $request->sex;
        $parent = $request->parent;
        $parent_tel = $request->parent_tel;
        $missing_time = $request->missing_time;
        $longitude = $request->longitude;
        $latitude = $request->latitude;


        $res = DB::table('missingkids')
            ->select('name')
            ->where([
                ['name',$name],
                ['state',1],
            ])
            ->first();

        if(!empty($res)){
            return $this->simpleJsonError('该儿童已存在');
        }

        $fields = [
            'name' => $name,
            'age' => $age,
            'sex' => $sex,
            'parent' => $parent,
            'parent_tel' => $parent_tel,
            'missing_time' => $missing_time,
            'missing_longitude' => $longitude,
            'missing_latitude' => $latitude,
            'created_at' => time(),
            'updated_at' => time(),
            'state' => 1,
            'find_token' => 0,
        ];

        $id = DB::table('missingkids')
            ->insertGetId($fields);

        if(!$id){
            return $this->simpleJsonError('表单填写失败，请重试');
        }

        session()->put([
           'kid_id' =>  $id,
        ]);
        return $this->simpleJsonSuccess('表单填写成功');
    }



    /**
     * @param Request $request
     * @return array
     * 添加图片和描述
     */
    public function addDescAndPic(Request $request){
        $id = session('kid_id');
        if(empty($id)){
            return $this->simpleJsonError('请先完成前面表单的填写');
        }

        $description = $request->input('description');
        $picture = $request->input('picture');

        if(empty($description) || empty($picture)){
            return $this->simpleJsonError('照片或描述不能为空');
        }

        $res = DB::table('missingkids')
            ->where('id',$id)
            ->update([
                'description' => $description,
                'picture' => $picture,
            ]);
        if(!$res){
            return $this->simpleJsonError('填写失败，请重试');
        }
        return $this->simpleJsonSuccess('填写成功');
    }


    /**
     * @param Request $request
     * @return array
     * 添加位置信息
     */
    public function addPosition(Request $request){
        $id = session('kid_id');
        if(empty($id)){
            return $this->simpleJsonError('请先完成前表单的填写');
        }

        $longitude = $request->input('longitude');
        $latitude = $request->input('latitude');
        $place = $request->input('place');

        if(empty($longitude) || empty($latitude)){
            return $this->simpleJsonError('经纬度不能为空');
        }

        $res = DB::table('missingkids')
            ->where('id',$id)
            ->update([
                'missing_longitude' => $longitude,
                'missing_latitude' => $latitude,
                'place' => $place,
            ]);

        if(!$res){
            return $this->simpleJsonError('位置信息添加失败，请重试');
        }

        return $this->simpleJsonSuccess('位置信息添加成功');
    }


//    /**
//     * @return array
//     * 获取全部丢失儿童信息
//     */
//    public function list(){
//        $data = DB::table('missingkids')
//            ->select('id','name','age','sex','parent','parent_tel','missing_time','description','picture')
//            ->orderBy('id')
//            ->paginate(15)
//            ->toArray();
//        return $this->simpleJsonSuccess($data);
//    }


    /**
     * @return array
     * 获取全部丢失儿童位置信息
     */
    public function getAllPosition(){
        $data = DB::table('missingkids')
            ->select('missing_longitude','missing_latitude')
            ->get();
        return $this->simpleJsonSuccess($data);
    }

    public function getOne(Request $request){
        $id = $request->input('id');
        if(!$this->isId($id)){
            return $this->simpleJsonError('请输入正确id');
        }

        $data = DB::table('missingkids')
            ->where([
                ['id',$id],
                ['state',1]
            ])
            ->select('name','age','sex','parent','parent_tel','missing_time','place','description')
            ->first();

        if(empty($data)){
            return $this->simpleJsonError('该儿童不存在');
        }

        return $this->simpleJsonSuccess($data);

    }


    /**
     * @return array
     * 获取全部丢失儿童全部信息
     */
    public function getAllInfo(){
        $data = DB::table('missingkids')
            ->select('id','name','age','sex','parent_tel')
            ->get();
        return $this->simpleJsonSuccess($data);
    }


    /**
     * 上传图片
     * @param Request $request
     * @return array
     */
    public function uploadPic(Request $request){
        $file = $request->file('source');
        $id = session('kid_id');
        $email = session('user_email');
        if(empty($id)){
            return $this->simpleJsonError('请先完成前面表单的填写');
        }

        if(empty($file)){
            return $this->simpleJsonError('照片或不能为空');
        }


        if($file->isValid()){
            $ext = $file->getClientOriginalExtension();
            $realPath = $file->getRealPath();
            $filename = date('Y-m-d-H-i-s') . '-' .uniqid() . '.' . $ext;

//            Storage::disk('upload')->makeDirectory('/' . $email);
//            Storage::disk('upload')->put('/' . $email . '/'.$filename,file_get_contents($realPath));
            $bool = Storage::disk('upload')->put($filename,file_get_contents($realPath));
            if(!$bool){
                return $this->simpleJsonError('上传失败');
            }

            return $this->simpleJsonSuccess('上传成功');
        }

        return $this->simpleJsonError('上传图片无效');

    }

    /**
     * 上传描述
     * @param Request $request
     * @return array
     */

    public function uploadDes(Request $request){
        $id = session('kid_id');
        $description = $request->input('description');
        if(empty($id)){
            return $this->simpleJsonError('请先完成前面表单的填写');
        }

        if(empty($description)){
            return $this->simpleJsonError('描述不能为空');
        }

        $res = DB::table('missingkids')
            ->where('id',$id)
            ->update([
                'description' => $description,
            ]);

        if(!$res){
            return $this->simpleJsonError('内容重复，请重新填写描述');
        }
        return $this->simpleJsonSuccess('填写成功');
    }



    public function getAllPic(Request $request){
        //获取当前的url
//        $realpath = str_replace('sysimg/','',Request::path());
//        $path = storage_path() . $realpath;
        $path = storage_path() . '/app/upload/2018-09-07-10-13-28-5b924f48cc84d.jpg';
        var_dump($path);
//        if(!file_exists($path)){
//            //报404错误
//            header("HTTP/1.1 404 Not Found");
//            header("Status: 404 Not Found");
//            exit;
//        }
//        //输出图片
//        header('Content-type: image/jpg');
//        echo file_get_contents($path);
    }

    public function News(Request $request){
        $kids_latitude = DB::table('missingkids')
            ->pluck('missing_latitude');
        $kids_len = $kids_latitude->count();

        $kids_longitude = DB::table('missingkids')
            ->pluck('missing_longitude');



        $user_longigude = DB::table('user')
            ->pluck('longitude');
        $user_len = $user_longigude->count();

        $user_latitude = DB::table('user')
            ->pluck('latitude');

        for($i=0; $i<$kids_len; $i++){
            for($j=0; $j<$user_len; $j++){
                if($this->GetDistance($kids_latitude[$i],$kids_longitude[$i],$user_latitude[$j],$user_longigude[$j],1) < 500 ){

                }
            }
        }

//        $latitude = DB::table('missingkids')
//            ->where('id',89)
//            ->value('missing_latitude');


//        echo $this->GetDistance(39.908156,116.4767, 39.908452,116.450479, 1);//输出距离/米
//        echo $this->GetDistance($kids_latitude[0],$kids_longitude[0],$user_latitude[1],$user_longigude[1],1);//输出距离/米

    }





}
