import org.example.HAL
import kotlin.test.*

class HALTest {

    @Test
    fun testIsBitTrue() {
        HAL.init()
        assertTrue(HAL.isBit(0b00000100, 0b00000100))
    }

    @Test
    fun testIsBitFalse() {
        HAL.init()
        assertFalse(HAL.isBit(0b00000100, 0b00000000))
    }

    @Test
    fun testReadBits() {
        HAL.init()
        val result = HAL.readBits(0b00001111, 0b10110110)
        assertEquals(0b00000110, result)
    }

    @Test
    fun testWriteBits() {
        HAL.init()
        HAL.lastValue = 0b10110000
        HAL.writeBits(0b00001111, 0b00000101)
        assertEquals(0b10110101, HAL.lastValue)
    }

    @Test
    fun testWriteBits2() {
        HAL.init()
        HAL.lastValue = 0b10110000
        HAL.writeBits(0b10101010, 0b11111111)
        assertEquals(0b10111010, HAL.lastValue)
    }


    @Test
    fun testSetBits() {
        HAL.init()
        HAL.lastValue = 0b00000000
        HAL.setBits(0b00000101)
        assertEquals(0b00000101, HAL.lastValue)
    }

    @Test
    fun testSetBits2() {
        HAL.init()
        HAL.lastValue = 0b00011100
        HAL.setBits(0b00000101)
        assertEquals(0b00011101, HAL.lastValue)
    }


    @Test
    fun testSetBits3() {
        HAL.init()
        HAL.lastValue = 0b11111100
        HAL.setBits(0b00000101)
        assertEquals(0b11111101, HAL.lastValue)
    }

    @Test
    fun testSetBits4() {
        HAL.init()
        HAL.lastValue = 0b00111100
        HAL.setBits(0b01010101)
        assertEquals(0b01111101, HAL.lastValue)
    }

    @Test
    fun testClrBits() {
        HAL.init()
        HAL.lastValue = 0b11111111
        HAL.clrBits(0b00000101)
        assertEquals(0b11111010, HAL.lastValue)
    }

    @Test
    fun testClrBits2() {
        HAL.init()
        HAL.lastValue = 0b11011111
        HAL.clrBits(0b00000101)
        assertEquals(0b11011010, HAL.lastValue)
    }


    @Test
    fun testClrBits3() {
        HAL.init()
        HAL.lastValue = 0b00000111
        HAL.clrBits(0b00000101)
        assertEquals(0b00000010, HAL.lastValue)
    }

    @Test
    fun testClrBits4() {
        HAL.init()
        HAL.lastValue = 0b00000111
        HAL.clrBits(0b00001111)
        assertEquals(0b00000000, HAL.lastValue)
    }
}