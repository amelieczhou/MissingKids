<?php
/**
 * Created by PhpStorm.
 * User: 13601
 * Date: 2018/7/18
 * Time: 14:37
 */
namespace App\Http\Controllers;


use App\Jobs\send;
use App\Student;


use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Session;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Validator;

class StudentController extends Controller{
    public function index(Request $request){
       $students = Student::paginate(5);
       return view('student.index',[
           'students' => $students,
       ]);
    }

    /**
     * @param Request $request
     * @param $v
     * @return \Illuminate\Http\RedirectResponse
     */
    public function create(Request $request){

        $student = new Student();
        if($request->isMethod('POST')) {

//            $this->validate($request,[
//                'Student.name' => 'required|min:2|max:20',
//                'Student.age' => 'required|integer',
//                'Student.sex' => 'required|integer',
//            ],[
//                'required' => ':attribute为必填项目',
//                'min' => ':attribute长度不符合要求',
//                'integer' => ':attribute要为整数',
//            ],[
//                'Student.name' => '姓名',
//                'Student.age'=>'年龄',
//                'Student.sex'=>'性别'
//            ]);
            $validator = Validator::make($request->input(),[
                'Student.name' => 'required|min:2|max:20',
                'Student.age' => 'required|integer',
                'Student.sex' => 'required|integer',
            ],[
                'required' => ':attribute为必填项目',
                'min' => ':attribute长度不符合要求',
                'integer' => ':attribute要为整数',
            ],[
                'Student.name' => '姓名',
                'Student.age'=>'年龄',
                'Student.sex'=>'性别'
            ]);
            if($validator->fails()){
                return redirect()->back()->withErrors($validator)->withInput();
            }


            $data = $request->input('Student');
            if (Student::create($data)) {
                return redirect('student/index')->with('success','添加成功');
            } else {
                return redirect()->back()->with('error','添加失败');
            }
        }

        return view('student.create',[
            'student' => $student
        ]);

    }

    public function save(Request $request){
        $data = $request->input('Student');

        $student = new Student();
        $student->name = $data['name'];
        $student->age = $data['age'];
        $student->sex = $data['sex'];

        if($student->save()){
            return redirect('student/index');
        }else{
            return redirect()->back();
        }
    }

    public function update(Request $request,$id){

        $student = Student::find($id);

        if($request ->isMethod('POST')){

            $this->validate($request,[
                'Student.name' => 'required|min:2|max:20',
                'Student.age' => 'required|integer',
                'Student.sex' => 'required|integer',
            ],[
                'required' => ':attribute为必填项目',
                'min' => ':attribute长度不符合要求',
                'integer' => ':attribute要为整数',
            ],[
                'Student.name' => '姓名',
                'Student.age'=>'年龄',
                'Student.sex'=>'性别'
            ]);


            $data = $request->input('Student');
            $student->name = $data['name'];
            $student->age = $data['age'];
            $student->sex = $data['sex'];

            $student->save();

//            if($student->save()){
//                return redirect('student/index')->with('success', '修改成功-'.$id);
//            }
        }
        return view('student.update',[
            'student' => $student,
        ]);
    }

    public function detail(Request $request,$id){

        $student = Student::find($id);
        return view('student.detail',[
            'student' => $student,
        ]);
    }

    public function delete($id){
        $student = Student::find($id);
        if($student->delete()){
            return redirect('student/' .
                'index')->with('error','删除失败'.$id);
            return redirect('student/index')->with('success','删除成功'.$id);
        }else{
        }
    }

}