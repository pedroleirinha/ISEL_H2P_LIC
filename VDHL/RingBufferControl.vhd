library ieee;
use ieee.std_logic_1164.all;

ENTITY RingBufferControl IS
	PORT(
		clk_in, DAV, CTS, full, empty: 			IN std_logic;
		Wreg, DAC, Wr, selPG, incPut, incGet:	OUT std_logic
	);
END RingBufferControl;

ARCHITECTURE Behaviour OF RingBufferControl IS
	
BEGIN
	

	
	

END Behaviour;