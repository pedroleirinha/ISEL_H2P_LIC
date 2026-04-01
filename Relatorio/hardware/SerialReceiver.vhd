library ieee;
use ieee.std_logic_1164.all;

ENTITY SerialReceiver IS
	PORT(
		SCLK, SDX, SS, CLEAR: 	IN std_logic;
		Q: 							OUT std_logic_vector(9 downto 0)
	);
END SerialReceiver;

ARCHITECTURE Behaviour OF SerialReceiver IS
	
	component ShiftRegister
		PORT(
			SCLK, enableShift, SerialIn, CLEAR: 	IN std_logic;
			Q: 									OUT std_logic_vector(9 downto 0)
		);
	end component;
	
	component HoldRegister
		PORT(
			SCLK, CLEAR: 	IN std_logic;
			D:					IN std_logic_vector(9 downto 0);
			Q:					OUT std_logic_vector(9 downto 0)
		);
	end component;
	
	signal shiftRegisterOut: std_logic_vector(9 downto 0);
	signal enableShift, holdShiftCLK: std_logic;

BEGIN

	enableShift 		<= SS;
	
	shiftRegister1: ShiftRegister port map(
		SCLK 				=> SCLK,
		enableShift 	=> enableShift,
		SerialIn 		=> SDX,		
		CLEAR				=> CLEAR,
		Q 					=> shiftRegisterOut		
	);
	
	hold: HoldRegister port map(
		SCLK 				=> SS,
		D 					=> shiftRegisterOut,
		CLEAR				=> CLEAR,
		Q 					=> Q		
	);

END Behaviour;