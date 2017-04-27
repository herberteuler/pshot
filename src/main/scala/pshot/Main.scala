package pshot

import scala.language.implicitConversions

import java.awt._
import java.io._
import java.time._
import java.time.format._
import java.util._
import java.util.concurrent._

import javax.imageio._

import configs.syntax._

object Main extends App {

  implicit def toTimerTask(f: => Unit): TimerTask = new TimerTask {
    override def run() = f
  }

  val dirNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  val fileNameFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

  def takeScreenshot(robot: Robot, rect: Rectangle, root: File,
                     format: String): Unit = {
    def calcDst(root: File, now: LocalDateTime): File = {
      val dir = new File(root, now.format(dirNameFormatter))
      new File(dir, s"${now.format(fileNameFormatter)}.$format")
    }
    val img = robot.createScreenCapture(rect)
    val dst = calcDst(root, LocalDateTime.now())
    dst.getParentFile.mkdirs()
    ImageIO.write(img, format, dst)
  }

  val timer = {
    val t = new Timer(true)
    sys.addShutdownHook { t.cancel() }
    t
  }

  val robot = new Robot
  val rect = new Rectangle(Toolkit.getDefaultToolkit.getScreenSize)
  val root = conf.get[File]("pshot.root").toOption.get
  val format = conf.get[String]("pshot.format").toOption.get
  val period = conf.get[Int]("pshot.period-mins").toOption.get * 60000
  timer.scheduleAtFixedRate(takeScreenshot(robot, rect, root, format), 0, period)

  val latch = {
    val l = new CountDownLatch(1)
    sys.addShutdownHook { l.countDown() }
    l
  }
  latch.await()
}
