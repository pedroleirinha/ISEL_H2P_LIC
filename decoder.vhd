library ieee;
use ieee.std_logic_1164.all;

ENTITY decoder IS
	PORT(
		A: OUT std_logic_vector (3 downto 0);
		S: IN std_logic_vector (1 downto 0)
	);
END decoder;

ARCHITECTURE Behaviour OF decoder IS
BEGIN
	A(0) <= '0' when S(0) = '0' and S(1) = '0' else '1';
	A(1) <= '0' when S(0) = '0' and S(1) = '1' else '1';
	A(2) <= '0' when S(0) = '1' and S(1) = '0' else '1';
	A(3) <= '0' when S(0) = '1' and S(1) = '1' else '1';

END Behaviour;