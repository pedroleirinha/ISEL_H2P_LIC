import org.example.HAL
import kotlin.test.*
import org.example.ENVIROMENT
import org.example.FakeUsbPort

class HALTest {

    @Test
    fun testIsBitTrue() {
        FakeUsbPort.portValue = 0b00000100
        HAL.init(enviroment = ENVIROMENT.TEST)
        HAL.readValue()
        assertTrue(HAL.isBit(0b00000100))
    }

    @Test
    fun testIsBitFalse() {
        FakeUsbPort.portValue = 0b00000000
        HAL.init(enviroment = ENVIROMENT.TEST)
        HAL.readValue()
        assertFalse(HAL.isBit(0b00000100))
    }

    @Test
    fun testReadBits() {
        FakeUsbPort.portValue = 0b10110110
        HAL.init(enviroment = ENVIROMENT.TEST)
        HAL.readValue()
        val result = HAL.readBits(0b00001111)
        assertEquals(0b00000110, result)
    }

    @Test
    fun testWriteBits() {
        FakeUsbPort.portValue = 0b10110000
        HAL.init(enviroment = ENVIROMENT.TEST)
        HAL.readValue()
        HAL.writeBits(0b00001111, 0b00000101)
        assertEquals(0b10110101, FakeUsbPort.portValue)
    }

    @Test
    fun testWriteBits2() {
        FakeUsbPort.portValue = 0b10110000
        HAL.init(enviroment = ENVIROMENT.TEST)
        HAL.readValue()
        HAL.writeBits(0b10101010, 0b11111111)
        assertEquals(0b10110101, FakeUsbPort.portValue)
    }


    @Test
    fun testSetBits() {
        FakeUsbPort.portValue = 0b00000000
        HAL.init(enviroment = ENVIROMENT.TEST)
        HAL.readValue()
        HAL.setBits(0b00000101)
        assertEquals(0b00000101, FakeUsbPort.portValue)
    }

    @Test
    fun testClrBits() {
        FakeUsbPort.portValue = 0b11111111
        HAL.init(enviroment = ENVIROMENT.TEST)
        HAL.readValue()
        HAL.clrBits(0b00000101)
        assertEquals(0b11111010, FakeUsbPort.portValue)
    }
}