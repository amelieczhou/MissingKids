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
            'created_at' => time(),
            'updated_at' => time(),
            'state' => 1,
            'find_token' => 0,
        ];

        $id = DB::table('missingkids')
            ->insertGetId($fields);

        if(!$id){
            return $this->simpleJsonError('插入失败,请重试');
        }

        session()->put([
           'kid_id' =>  $id,
        ]);
        return $this->simpleJsonSuccess('插入成功',session('kid_id'));
    }

    public function addDescAndPic(Request $request){
        $id = session('kid_id');
        if(empty($id)){
            return $this->simpleJsonError('please register the page before');
        }

        $description = $request->input('description');
        $picture = $request->input('picture');

        if(empty($description) || empty($picture)){
//            return $this->simpleJsonError('照片或描述不能为空');
            return $this->simpleJsonError('empty');
        }

        $res = DB::table('missingkids')
            ->where('id',$id)
            ->update([
                'description' => $description,
                'picture' => $picture,
            ]);

        if(!$res){
//            return $this->simpleJsonError('插入内容与原内容重复');
            return $this->simpleJsonError('content the same',session('kid_id'));
        }

        return $this->simpleJsonSuccess('插入成功');
    }

    public function edit(Request $request){

    }


    public function list(){
        $data = DB::table('missingkids')
            ->select('id','name','age','sex','parent','parent_tel','missing_time','description','picture')
            ->orderBy('id')
            ->paginate(15)
            ->toArray();
        return $this->simpleJsonSuccess($data);
    }


    public function del(){

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
            ->select('id','name','age','sex','parent','parent_tel','missing_time','description','picture')
            ->first();

        if(empty($data)){
            return $this->simpleJsonError('该儿童不存在');
        }

        return $this->simpleJsonSuccess($data);


    }
}