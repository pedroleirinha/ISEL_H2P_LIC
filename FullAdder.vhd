library ieee;
use ieee.std_logic_1164.all;

ENTITY FullAdder IS
	PORT(
		A,B,CBi: IN std_logic;
		S, CO: OUT std_logic
	);
END FullAdder;

ARCHITECTURE Behaviour OF FullAdder IS

signal addResult: std_logic;

BEGIN
	addResult <= A XOR B;
	
	
	S <= (addResult) XOR CBi;
	CO <= (A AND B) OR (addResult AND CBi);
	
END Behaviour;