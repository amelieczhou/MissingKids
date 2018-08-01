<?php

use Illuminate\Database\Seeder;

class StduentTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        DB::table('students')->insert([
            ['name'=>'www'],
            ['name'=>'aaa'],
        ]);
    }
}
