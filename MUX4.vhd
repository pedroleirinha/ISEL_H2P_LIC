library ieee;
use ieee.std_logic_1164.all;

ENTITY MUX4 IS
	PORT(
		A,B: IN std_logic_vector (3 downto 0);
		S: IN std_logic;
		Y: OUT std_logic_vector (3 downto 0)
	);
END MUX4;

ARCHITECTURE Behaviour OF MUX4 IS
BEGIN
	Y(0) <= (A(0) AND NOT S) OR (B(0) AND S);
	Y(1) <= (A(1) AND NOT S) OR (B(1) AND S);
	Y(2) <= (A(2) AND NOT S) OR (B(2) AND S);
	Y(3) <= (A(3) AND NOT S) OR (B(3) AND S);
	
END Behaviour;