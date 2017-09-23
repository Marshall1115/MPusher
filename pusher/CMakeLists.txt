# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
set(lib_src_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC -fpermissive -std=c++11 -D__STDC_CONSTANT_MACROS -DFIXED_POINT -DUSE_KISS_FFT -DEXPORT="" -UHAVE_CONFIG_H ")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -O2 -DSYS=posix -DNO_CRYPTO")

include_directories(
     ${CMAKE_SOURCE_DIR}/src/main/cpp/include
)

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
            src/main/cpp/main.cpp
            src/main/cpp/live_native.cpp
            src/main/cpp/JNILoad.cpp
            src/main/cpp/JNIUtil.cpp
             src/main/cpp/pusher.cpp
              )
#x264
add_library(x264_lib SHARED IMPORTED)
set_target_properties(x264_lib PROPERTIES IMPORTED_LOCATION
                     ${lib_src_DIR}/libx264.so)
#librtmp
add_library(rtmp_lib SHARED IMPORTED)
set_target_properties(rtmp_lib PROPERTIES IMPORTED_LOCATION
                     ${lib_src_DIR}/librtmp.a)
#faac
add_library(faac_lib STATIC IMPORTED)
set_target_properties(faac_lib PROPERTIES IMPORTED_LOCATION
                       ${lib_src_DIR}/libfaac.a)
#libyuv
add_library(yuv_lib SHARED IMPORTED)
set_target_properties(yuv_lib PROPERTIES IMPORTED_LOCATION
                       ${lib_src_DIR}/libyuv.so)
# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib
                       x264_lib
                       faac_lib
                       rtmp_lib
                       yuv_lib
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )