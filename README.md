# Hollywood
[![Build Status](https://travis-ci.org/serandel/hollywood.svg?branch=master)](https://travis-ci.org/serandel/hollywood)

An opinionated, Android-first, Java functional reactive framework inspired by Cycle.js, but not that much

## Core

Core library, with no platform-specific code.

## Android

Android library, extending Hollywood with Android code.

## Sample-app

What Now?, the sample application for Hollywood, is a twist in the traditional to-do app used to showcase every framework ever, featuring Hollywood Android.

## Developer beware!

- Hollywood, Hollywood Android and What Now use extensively Java 1.8. For the projects to work, Retrolambda and Jack support had to be added. Jack wasn't enough because Java libraries are imported via Jill, and Jill can't manage .class files for Java 7/8 (at least, not the ones with *invokedynamic* in their bytecode). It's worth the trouble because life is to short to use outdated code in your pet projects. That's what the day job is for. ;)
- Hollywood Android uses Timber instead of Android.log. It's a tiny library, and useful enough to merit being a dependency.

## TODOs

Explain how Retrolambda was added to core module so we could use the library in Android. Pure Java applications could and should disable it and simply use a 1.8 JDK.
