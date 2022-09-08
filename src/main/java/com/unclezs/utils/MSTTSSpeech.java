package com.unclezs.utils;

import cn.hutool.core.io.FileUtil;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;

/**
 * windows 文本转语音工具
 *
 * @author https://blog.csdn.net/weixin_33873846/article/details/92559209
 * @date 2020-5-17
 */
@Slf4j
public class MSTTSSpeech {
    /**
     * 声音：1到100
     */
    private int volume = 100;
    /**
     * 频率：-10到10
     */
    private int rate = 0;
    /**
     * 语音库序号
     */
    private int voice = 0;
    /**
     * 输出设备序号
     */
    private int audio = 0;
    private ActiveXComponent ax;
    /**
     * 声音对象
     */
    private Dispatch spVoice;
    /**
     * 音频格式对象
     */
    private Dispatch spAudioFormat = null;
    /**
     * 音频输出对象
     */
    private Dispatch dispatch = null;
    /**
     * 音频的输出格式，默认为：SAFT22kHz16BitMono
     */
    private int formatType = 22;

    public MSTTSSpeech() {
        try {
            ComThread.InitSTA();
            ax = new ActiveXComponent("Sapi.SpVoice");
            spVoice = ax.getObject();
        } catch (Throwable e) {
            log.error("初始化朗读声源失败：{}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        MSTTSSpeech speech = new MSTTSSpeech();
        String text = FileUtil.readUtf8String("D:\\java\\NovelHarvester\\1.txt");
        speech.setFormatType(6);
        // speech.setRate(-1);
        speech.saveToWav(text, "D:\\java\\NovelHarvester\\test.wav");
    }

    /**
     * 改变语音库
     *
     * @param voice 语音库序号
     */
    public void changeVoice(int voice) {
        if (this.voice != voice) {
            this.voice = voice;
        }
        try {
            Dispatch voiceItems = Dispatch.call(spVoice, "GetVoices").toDispatch();
            int count = Integer.valueOf(Dispatch.call(voiceItems, "Count").toString());
            if (count > 0) {
                Dispatch voiceItem = Dispatch.call(voiceItems, "Item", new Variant(this.voice)).toDispatch();
                Dispatch.put(spVoice, "Voice", voiceItem);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 改变音频输出设备
     *
     * @param audio 音频设备序号
     */
    public void changeAudioOutput(int audio) {
        if (this.audio != audio) {
            this.audio = audio;
        }
        try {
            Dispatch audioOutputs = Dispatch.call(spVoice, "GetAudioOutputs").toDispatch();
            int count = Integer.valueOf(Dispatch.call(audioOutputs, "Count").toString());
            if (count > 0) {
                Dispatch audioOutput = Dispatch.call(audioOutputs, "Item", new Variant(this.audio)).toDispatch();
                Dispatch.put(spVoice, "AudioOutput", audioOutput);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 播放语音
     *
     * @param text 要转换成语音的文本
     */
    public void speak(String text) {
        this.speak(text, 0);
    }

    /**
     * 停止播放语音
     */
    public void stop() {
        Dispatch.call(spVoice, "Pause");
    }

    /**
     * 播放语音
     *
     * @param text 要转换成语音的文本
     * @param type 类型0:播放，1：停止
     */
    private void speak(String text, int type) {
        switch (type) {
            case 0:
                try {
                    // 调整音量和读的速度
                    Dispatch.put(spVoice, "Volume", new Variant(this.volume));
                    Dispatch.put(spVoice, "Rate", new Variant(this.rate));
                    // 设置音频格式类型
                    if (spAudioFormat == null) {
                        ax = new ActiveXComponent("Sapi.SpAudioFormat");
                        spAudioFormat = ax.getObject();
                        ax = new ActiveXComponent("Sapi.SpMMAudioOut");
                        dispatch = ax.getObject();
                    }
                    Dispatch.put(spAudioFormat, "Type", new Variant(this.formatType));
                    Dispatch.putRef(dispatch, "Format", spAudioFormat);
                    Dispatch.put(spVoice, "AllowAudioOutputFormatChangesOnNextSet", new Variant(false));
                    Dispatch.putRef(spVoice, "AudioOutputStream", dispatch);
                    // 开始朗读
                    Dispatch.call(spVoice, "Speak", new Variant(text));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    Dispatch.call(spVoice, "Speak", new Variant(text), new Variant(2));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取系统中所有的语音库名称数组
     *
     * @return String[]
     */
    public String[] getVoices() {
        String[] voices = null;
        try {
            Dispatch voiceItems = Dispatch.call(spVoice, "GetVoices").toDispatch();
            int count = Integer.valueOf(Dispatch.call(voiceItems, "Count").toString());
            if (count > 0) {
                voices = new String[count];
                for (int i = 0; i < count; i++) {
                    Dispatch voiceItem = Dispatch.call(voiceItems, "Item", new Variant(i)).toDispatch();
                    String voice = Dispatch.call(voiceItem, "GetDescription").toString();
                    voices[i] = voice;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return voices;
    }

    /**
     * 获取音频输出设备名称数组
     *
     * @return String[]
     */
    public String[] getAudioOutputs() {
        String[] result = null;
        try {
            Dispatch audioOutputs = Dispatch.call(spVoice, "GetAudioOutputs").toDispatch();
            int count = Integer.valueOf(Dispatch.call(audioOutputs, "Count").toString());
            if (count > 0) {
                result = new String[count];
                for (int i = 0; i < count; i++) {
                    Dispatch voiceItem = Dispatch.call(audioOutputs, "Item", new Variant(i)).toDispatch();
                    String voice = Dispatch.call(voiceItem, "GetDescription").toString();
                    result[i] = voice;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将文字转换成音频信号，然后输出到.WAV文件
     *
     * @param text     文本字符串
     * @param filePath 输出文件路径
     */
    public void saveToWav(String text, String filePath) {
        FileUtil.touch(filePath);
        // 创建输出文件流对象
        ax = new ActiveXComponent("Sapi.SpFileStream");
        //音频文件输出流对象，在读取或保存音频文件时使用
        Dispatch spFileStream = ax.getObject();
        // 创建音频流格式对象
        if (spAudioFormat == null) {
            ax = new ActiveXComponent("Sapi.SpAudioFormat");
            spAudioFormat = ax.getObject();
        }
        // 设置音频流格式类型
        Dispatch.put(spAudioFormat, "Type", new Variant(this.formatType));
        // 设置文件输出流的格式
        Dispatch.putRef(spFileStream, "Format", spAudioFormat);
        // 调用输出文件流对象的打开方法，创建一个.wav文件
        Dispatch.call(spFileStream, "Open", new Variant(filePath), new Variant(3), new Variant(true));
        // 设置声音对象的音频输出流为输出文件流对象
        Dispatch.putRef(spVoice, "AudioOutputStream", spFileStream);
        // 调整音量和读的速度
        Dispatch.put(spVoice, "Volume", new Variant(this.volume));
        Dispatch.put(spVoice, "Rate", new Variant(this.rate));
        // 开始朗读
        Dispatch.call(spVoice, "Speak", new Variant(text));
        // 关闭输出文件流对象，释放资源
        Dispatch.call(spFileStream, "Close");
        Dispatch.putRef(spVoice, "AudioOutputStream", null);
    }

    /**
     * @return the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * @return the rate
     */
    public int getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * @return the voice
     */
    public int getVoice() {
        return voice;
    }

    /**
     * @param voice the voice to set
     */
    public void setVoice(int voice) {
        this.voice = voice;
    }

    /**
     * @return the audio
     */
    public int getAudio() {
        return audio;
    }

    /**
     * @param audio the audio to set
     */
    public void setAudio(int audio) {
        this.audio = audio;
    }

    /**
     * @return the ax
     */
    public ActiveXComponent getAx() {
        return ax;
    }

    /**
     * @param ax the ax to set
     */
    public void setAx(ActiveXComponent ax) {
        this.ax = ax;
    }

    /**
     * @return the formatType
     */
    public int getFormatType() {
        return formatType;
    }

    /**
     * 设置音频输出格式类型<br>
     * SAFTDefault = -1<br>
     * SAFTNoAssignedFormat = 0<br>
     * SAFTText = 1<br>
     * SAFTNonStandardFormat = 2<br>
     * SAFTExtendedAudioFormat = 3<br>
     * // Standard PCM wave formats<br>
     * SAFT8kHz8BitMono = 4<br>
     * SAFT8kHz8BitStereo = 5<br>
     * SAFT8kHz16BitMono = 6<br>
     * SAFT8kHz16BitStereo = 7<br>
     * SAFT11kHz8BitMono = 8<br>
     * SAFT11kHz8BitStereo = 9<br>
     * SAFT11kHz16BitMono = 10<br>
     * SAFT11kHz16BitStereo = 11<br>
     * SAFT12kHz8BitMono = 12<br>
     * SAFT12kHz8BitStereo = 13<br>
     * SAFT12kHz16BitMono = 14<br>
     * SAFT12kHz16BitStereo = 15<br>
     * SAFT16kHz8BitMono = 16<br>
     * SAFT16kHz8BitStereo = 17<br>
     * SAFT16kHz16BitMono = 18<br>
     * SAFT16kHz16BitStereo = 19<br>
     * SAFT22kHz8BitMono = 20<br>
     * SAFT22kHz8BitStereo = 21<br>
     * SAFT22kHz16BitMono = 22<br>
     * SAFT22kHz16BitStereo = 23<br>
     * SAFT24kHz8BitMono = 24<br>
     * SAFT24kHz8BitStereo = 25<br>
     * SAFT24kHz16BitMono = 26<br>
     * SAFT24kHz16BitStereo = 27<br>
     * SAFT32kHz8BitMono = 28<br>
     * SAFT32kHz8BitStereo = 29<br>
     * SAFT32kHz16BitMono = 30<br>
     * SAFT32kHz16BitStereo = 31<br>
     * SAFT44kHz8BitMono = 32<br>
     * SAFT44kHz8BitStereo = 33<br>
     * SAFT44kHz16BitMono = 34<br>
     * SAFT44kHz16BitStereo = 35<br>
     * SAFT48kHz8BitMono = 36<br>
     * SAFT48kHz8BitStereo = 37<br>
     * SAFT48kHz16BitMono = 38<br>
     * SAFT48kHz16BitStereo = 39<br>
     * <br>
     * // TrueSpeech format<br>
     * SAFTTrueSpeech_8kHz1BitMono = 40<br>
     * // A-Law formats<br>
     * SAFTCCITT_ALaw_8kHzMono = 41<br>
     * SAFTCCITT_ALaw_8kHzStereo = 42<br>
     * SAFTCCITT_ALaw_11kHzMono = 43<br>
     * SAFTCCITT_ALaw_11kHzStereo = 4<br>
     * SAFTCCITT_ALaw_22kHzMono = 44<br>
     * SAFTCCITT_ALaw_22kHzStereo = 45<br>
     * SAFTCCITT_ALaw_44kHzMono = 46<br>
     * SAFTCCITT_ALaw_44kHzStereo = 47<br>
     * <br>
     * // u-Law formats<br>
     * SAFTCCITT_uLaw_8kHzMono = 48<br>
     * SAFTCCITT_uLaw_8kHzStereo = 49<br>
     * SAFTCCITT_uLaw_11kHzMono = 50<br>
     * SAFTCCITT_uLaw_11kHzStereo = 51<br>
     * SAFTCCITT_uLaw_22kHzMono = 52<br>
     * SAFTCCITT_uLaw_22kHzStereo = 53<br>
     * SAFTCCITT_uLaw_44kHzMono = 54<br>
     * SAFTCCITT_uLaw_44kHzStereo = 55<br>
     * SAFTADPCM_8kHzMono = 56<br>
     * SAFTADPCM_8kHzStereo = 57<br>
     * SAFTADPCM_11kHzMono = 58<br>
     * SAFTADPCM_11kHzStereo = 59<br>
     * SAFTADPCM_22kHzMono = 60<br>
     * SAFTADPCM_22kHzStereo = 61<br>
     * SAFTADPCM_44kHzMono = 62<br>
     * SAFTADPCM_44kHzStereo = 63<br>
     * <br>
     * // GSM 6.10 formats<br>
     * SAFTGSM610_8kHzMono = 64<br>
     * SAFTGSM610_11kHzMono = 65<br>
     * SAFTGSM610_22kHzMono = 66<br>
     * SAFTGSM610_44kHzMono = 67<br>
     * // Other formats<br>
     * SAFTNUM_FORMATS = 68<br>
     *
     * @param formatType 音频输出格式类型
     */
    public void setFormatType(int formatType) {
        this.formatType = formatType;
    }
}
