/**
 *
 * PairedDongleTest
 * Ledger wallet
 *
 * Created by Pierre Pollastri on 06/02/15.
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ledger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.ledger.ledgerwallet.model

import android.test.InstrumentationTestCase
import com.ledger.ledgerwallet.models.PairedDongle
import com.ledger.ledgerwallet.utils.logs.Logger
import junit.framework.Assert
import org.spongycastle.util.encoders.Hex
import scala.collection.mutable

class PairedDongleTest extends InstrumentationTestCase {

  def testShouldCreateAndGet(): Unit = {
    implicit val context = getInstrumentation.getTargetContext
    val pairingId = "a test pairing id"
    val name = "A super name for an amazing dongle"
    val pairingKey = Hex.decode("6032d5032c905f39447bc3f28a043a99")
    val dongle = PairedDongle.create(pairingId, name, pairingKey)
    Assert.assertEquals(pairingId, dongle.id.get)
    Assert.assertEquals(name, dongle.name.get)
    Assert.assertEquals(Hex.toHexString(pairingKey), Hex.toHexString(dongle.pairingKey.get.secret))
  }

  def testShouldCreateAndGetFromPreferences(): Unit = {
    implicit val context = getInstrumentation.getTargetContext
    val pairingId = "a test pairing id"
    val name = "A super name for an amazing dongle"
    val pairingKey = Hex.decode("6032d5032c905f39447bc3f28a043a99")
    PairedDongle.create(pairingId, name, pairingKey)
    val dongle = PairedDongle.get(pairingId)
    Assert.assertTrue(dongle.isDefined)
    Assert.assertEquals(pairingId, dongle.get.id.get)
    Assert.assertEquals(name, dongle.get.name.get)
    Assert.assertEquals(Hex.toHexString(pairingKey), Hex.toHexString(dongle.get.pairingKey.get.secret))
    Logger.d(Hex.toHexString(pairingKey) + " <> " + Hex.toHexString(dongle.get.pairingKey.get.secret))
  }

  def testShouldCreateAndGetFromPreferencesMuktipleDongle(): Unit = {
    implicit val context = getInstrumentation.getTargetContext

    val testSet = mutable.Map[String, (String, String)]()

    testSet("a test pairing id") = ("A super name for an amazing dongle", "6032d5032c905f39447bc3f28a043a99")
    testSet("pairing_1") = ("A first name", "aa32d5032c905f39447bc3f28a043a994ccddb8f")
    testSet("pairing_2") = ("A second name", "bb32d5032c905f39447bc3f28a043a994ccddb8f")
    testSet("pairing_3") = ("A third name", "cc32d5032c905f39447bc3f28a043a994ccddb8f")
    testSet("pairing_4") = ("A fourth name", "dd32d5032c905f39447bc3f28a043a994ccddb8f")
    testSet("pairing_5") = ("A fifth name", "ee32d5032c905f39447bc3f28a043a994ccddb8f")
    testSet("pairing_6") = ("A sixth name", "ff32d5032c905f39447bc3f28a043a994ccddb8f")

    testSet foreach {
      case (id, value) =>
        PairedDongle.create(id, value._1, Hex.decode(value._2))
    }

    val dongles = PairedDongle.all

    Assert.assertEquals(testSet.size, dongles.length)
    for (dongle <- dongles) {
      val sample = testSet(dongle.id.get)
      Assert.assertNotNull(sample)
      Assert.assertEquals(sample._1, dongle.name.get)
      Assert.assertEquals(sample._2, Hex.toHexString(dongle.pairingKey.get.secret))
    }
  }

}
