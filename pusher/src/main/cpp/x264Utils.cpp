extern "C" {
#include <x264.h>
}

// Created by Administrator on 2017/9/22.
//
x264_picture_t pic;
x264_picture_t pic_out;//它输出的是编码中的重建图像，可以在试调中使用
int initX264(int width, int height, int bitrate, x264_param_t *params, x264_t **x264_handler) {
    x264_nal_t *nal;

    //参数2 ：编码速度 以压缩比的选项集合。较慢的预设将提供更好的压缩
    //ultrafast,superfast, veryfast, faster, fast, medium, slow, slower, veryslow, placebo.
    //参数3：编码方式  指定片子的类型，是和视觉优化的参数  zerolatency：零延迟
    if (x264_param_default_preset(params, "ultrafast", "zerolatency") < 0)
        return -1;

    params->i_width = width;
    params->i_height = height;
    params->b_vfr_input = 0;//If 1, use timebase and timestamps for ratecontrol purpose If 0, use fps only.
    //SPS/PPS 放到关键帧前面 该参数设置是让每个I帧都附带sps/pps。  提高视频的纠错能力
    // 参考http://www.dzsc.com/data/html/2011-3-28/89320.html
    params->b_repeat_headers = 1;
    params->b_annexb = 1;//这里设置为0，是为了使编码后的NAL统一有4字节的起始码，便于处理，否则会同时有3字节和4字节的起始码，很麻烦  设置为0 h264文件不能用
    params->i_threads = 1;

    int fps = 25;
    params->i_csp = X264_CSP_I420;
    params->i_width = width;
    params->i_height = height;
    params->rc.i_bitrate = bitrate / 1000;
    params->rc.i_rc_method = X264_RC_ABR; //参数i_rc_method表示码率控制，CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
    params->rc.i_vbv_buffer_size = bitrate / 1000; //设置了i_vbv_max_bitrate必须设置此参数，码率控制区大小,单位kbps
    params->rc.i_vbv_max_bitrate = bitrate / 1000 * 1.2; //瞬时最大码率
    params->i_keyint_max = fps * 2;//GOP 通常为 FPS 的倍数
    params->i_fps_num = fps; //* 帧率分子
    params->i_fps_den = 1; //* 帧率分母
    params->i_level_idc = 30;//level
    params->i_threads = 1;
    params->i_timebase_den = params->i_fps_num;
    params->i_timebase_num = params->i_fps_den;
    params->b_repeat_headers = 1;

    //    恒定码率，会尽量控制在固定码率
//    param.rc.i_rc_method = X264_RC_CRF;
//    param.rc.i_bitrate = bps / 1000; // 码率(比特率,单位Kbps)
//    param.rc.i_vbv_max_bitrate = bps / 1000 * 1.2; //瞬时最大码率
//    param.i_level_idc = 51;//level 5.1
//    param.b_vfr_input = 0;
//    param.i_fps_num = 25; // 帧率分子
//    param.i_fps_den = 1; // 帧率分母
//    param.i_timebase_den = param.i_fps_num;
//    param.i_timebase_num = param.i_fps_den;
//    param.i_threads = 1;//并行编码线程数量，0默认为多线程

    if (x264_param_apply_profile(params, "baseline") < 0)//没有b帧
        return -1;

    pic.img.i_csp = X264_CSP_I420;//发现设置nv21后 pic.img.plane[3]为null
    if (x264_picture_alloc(&pic, params->i_csp, params->i_width, params->i_height) < 0)
        return -1;
    x264_t *handler = x264_encoder_open(params);
    *x264_handler = handler;
    if (!x264_handler) {
        return -1;
    }
    return 1;
}

