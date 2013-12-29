import com.musicg.wave.Wave
import java.io.{FileInputStream, File}
import javax.sound.sampled.{AudioSystem, AudioFileFormat}
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.filefilter.{SuffixFileFilter, TrueFileFilter, FileFilterUtils, IOFileFilter}
import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Created by eric on 26/12/13.
 */
object MD5FileCalculator {

  def calculateMD5forFile (file: File) : String =  {
    val fis = new FileInputStream(file)
    DigestUtils.md5Hex(fis)
  }

}


object MusicMatch {

  val md5Map = mutable.HashMap.empty[String,String]
  val fingerPrintMap = mutable.HashMap.empty[Array[Byte], String]
  var audioSuffixes = List(".aac", ".mp3", ".wav", ".ogg", ".midi", ".3gp", ".mp4", ".amr", ".flac", ".m4a")     // .m4a is not supported by JLayer. Lne should use JAAD but there are some cnflict

  def main(args: Array[String]) {
    checkForDuplicates(args(0))
  }

  def checkForDuplicates (path: String) = {
    val fileIt = filesIterator(path)

    //println ("Check Duplicates across " + fileIt.size + " in " + path)

    for ( file <- fileIt) {
      print("Analyzing : " + file.getName + "\r")
      // extractMetaData(file)
      val md5 = MD5FileCalculator.calculateMD5forFile(file)
      if (md5Map contains md5) {
        // This is a duplicate
        println("Files Duplicates: " + file.getAbsolutePath() + " - " + md5Map.get(md5).get +  " are duplicates")
      }
      else {
        md5Map.put(md5, file.getAbsolutePath)
        // check fingerprint
        /*
        val wave = new Wave(file.getAbsolutePath)
        val fingerPrint = wave.getFingerprint
        if (fingerPrintMap.contains(fingerPrint)) {
          // This sounds a duplicate
          println("Sounds Duplicates: " + file.getAbsolutePath() + " - " + fingerPrintMap.get(fingerPrint).get +  " are duplicates")
        }
        else {
          fingerPrintMap.put(fingerPrint, file.getAbsolutePath)
        }
        */
      }
    }
  }

  def filesIterator (path: String) =  {
    FileUtils.iterateFiles(new File(path), new SuffixFileFilter(audioSuffixes), TrueFileFilter.INSTANCE)
  }

  def extractMetaData(file: File) = {
    val inputFileFormat = AudioSystem.getAudioFileFormat(file)

    val ais = AudioSystem.getAudioInputStream(file);

    val audioFormat = ais.getFormat()

    println(file.getAbsolutePath)
    System.out.println("File Format Type: "+inputFileFormat.getType());
    System.out.println("File Format String: "+inputFileFormat.toString());
    System.out.println("File lenght: "+inputFileFormat.getByteLength());
    System.out.println("Frame length: "+inputFileFormat.getFrameLength());
    System.out.println("Channels: "+audioFormat.getChannels());
    System.out.println("Encoding: "+audioFormat.getEncoding());
    System.out.println("Frame Rate: "+audioFormat.getFrameRate());
    System.out.println("Frame Size: "+audioFormat.getFrameSize());
    System.out.println("Sample Rate: "+audioFormat.getSampleRate());
    System.out.println("Sample size (bits): "+audioFormat.getSampleSizeInBits());
    System.out.println("Big endian: "+audioFormat.isBigEndian());
    System.out.println("Audio Format String: "+audioFormat.toString());

    val properties = audioFormat.properties()
    println("Bit rate: " + properties.get("bitrate") + " bps")


    /*
    AudioInputStream encodedASI = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, ais);

    try{
      int i = AudioSystem.write(encodedASI, AudioFileFormat.Type.WAVE, new File("c:\\converted.wav"));
      System.out.println("Bytes Written: "+i);
    }catch(Exception e){
      e.printStackTrace();
    }
    */

  }
}