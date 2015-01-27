package com.cedarsoftware.util.io

import com.cedarsoftware.util.IOUtilities

/**
 * Useful utilities for use in unit testing.
 *
 * @author John DeRegnaucourt (jdereg@gmail.com)
 *         <br/>
 *         Copyright (c) Cedar Software LLC
 *         <br/><br/>
 *         Licensed under the Apache License, Version 2.0 (the "License")
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br/><br/>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br/><br/>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
class TestUtil
{
    private static long totalJsonWrite;
    private static long totalObjWrite;
    private static long totalJsonRead;
    private static long totalObjRead;

    private static long outputStreamFailCount;
    private static boolean debug = false;

    static boolean isDebug() { return debug }

    static String getResource(String name)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192)
        URL url = TestJsonReaderWriter.class.getResource('/' + name)
        IOUtilities.transfer(new File(url.file), out)
        new String(out.toByteArray(), 'UTF-8')
    }

    static String getJsonString(Object obj) throws Exception
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream()
        JsonWriter jsonWriter = new JsonWriter(bout)
        long startWrite1 = System.nanoTime()
        jsonWriter.write(obj)
        jsonWriter.flush()
        jsonWriter.close()
        long endWrite1 = System.nanoTime()
        String json = new String(bout.toByteArray(), 'UTF-8')

        try
        {
            bout = new ByteArrayOutputStream()
            ObjectOutputStream out = new ObjectOutputStream(bout)
            long startWrite2 = System.nanoTime()
            out.writeObject(obj)
            out.flush()
            out.close()
            long endWrite2 = System.nanoTime()

            totalJsonWrite += endWrite1 - startWrite1;
            totalObjWrite += endWrite2 - startWrite2;
            double t1 = (endWrite1 - startWrite1) / 1000000.0
            double t2 = (endWrite2 - startWrite2) / 1000000.0
            if (debug)
            {
                println('JSON write time = ' + t1 + ' ms')
                println('ObjectOutputStream time = ' + t2 + ' ms')
            }
        }
        catch (Exception e)
        {
            outputStreamFailCount++;
        }

        return json;
    }

    static Object readJsonObject(String json) throws Exception
    {
        long startRead1 = System.nanoTime()
        Object o = JsonReader.jsonToJava(json)
        long endRead1 = System.nanoTime()

        try
        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream()
            ObjectOutputStream out = new ObjectOutputStream(bout)
            out.writeObject(o)
            out.flush()
            out.close()

            long startRead2 = System.nanoTime()
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray())
            ObjectInputStream input = new ObjectInputStream(bin)
            input.readObject()
            input.close()
            long endRead2 = System.nanoTime()

            totalJsonRead += endRead1 - startRead1;
            totalObjRead += endRead2 - startRead2;
            double t1 = (endRead1 - startRead1) / 1000000.0;
            double t2 = (endRead2 - startRead2) / 1000000.0;
            if (debug)
            {
                println("JSON  read time  = " + t1 + " ms")
                println("ObjectInputStream time = " + t2 + " ms")
            }
        }
        catch (Exception e)
        {
            outputStreamFailCount++;
        }

        return o;
    }

    static void printLine(String s)
    {
        if (debug)
        {
            println s;
        }
    }

    static void getTimings()
    {
        println("Total json-io read  = " + (totalJsonRead / 1000000.0) + " ms")
        println("Total json-io write = " + (totalJsonWrite / 1000000.0) + " ms")
        println("Total ObjectStream read  = " + (totalObjRead / 1000000.0) + " ms")
        println("Total ObjectStream write = " + (totalObjWrite / 1000000.0) + " ms")
        println("JDK InputStream/OutputStream fail count = " + outputStreamFailCount)
    }
}