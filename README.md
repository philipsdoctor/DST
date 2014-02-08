# DST

A Dead Simple Template language... it will either add or subtract an hour from your dev time!

## Usage

Leiningen 

    [org.clojars.pdoctor/dst "0.1.0-SNAPSHOT"]

Sample Code

    (ns example.core
      (:require [dst.core :refer [generate-template]]))

    (defn foo 
      []  
      (let [my-template (generate-template "Hello ${name}")]
        (prn (my-template {:name "Phil"}))))

Syntax

The generate-template function expects a string template.  The template contains text with interspersed symbol names.  A symbol is understood as text in the template surrounded by ${}.  generate-template will return a function that expects a dict with a keyword for every symbol passed in the template.

A simple example (generate-template "Hello ${name}") will return a function similar to the following but with validation:

    (fn [{name :name}] (str "Hello " name))

The ' character can be used as an escape, example (generate-template "Hello '${name}") will generate a function that takes an empty map and returns "Hello ${name}". 

## License

The MIT License (MIT)

Copyright © 2014 Philip S Doctor

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

