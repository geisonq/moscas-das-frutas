LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=STATIC

include C:/OpenCV/OpenCV310Android/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES :=native_processing.cpp
LOCAL_LDLIBS +=  -llog -ldl
LOCAL_MODULE:= native_processing

include $(BUILD_SHARED_LIBRARY)
