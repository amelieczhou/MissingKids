<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Tuisong;

class KidsController extends Controller
{
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


    public function list(){
        $data = DB::table('missingkids')
            ->select('id','name','age','sex','parent','parent_tel','missing_time','description','picture')
            ->orderBy('id')
            ->paginate(15)
            ->toArray();
        return $this->simpleJsonSuccess($data);
    }

    public function edit(){

    }


    public function del(){

    }

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

    public function getAllInfo(){
        $data = DB::table('missingkids')
            ->select('id','name','age','sex','parent_tel')
            ->get();
        return $this->simpleJsonSuccess($data);
    }





}
