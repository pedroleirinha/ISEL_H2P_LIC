library IEEE;
use IEEE.std_logic_1164.all;

entity MUX4_2L1_tb is
end MUX4_2L1_tb;

architecture MUX4_2L1_tb_arch of MUX4_2L1_tb is

component MUX4_2L1 port(
	A: IN std_logic_vector (3 downto 0);
	S: IN std_logic_vector (1 downto 0);
	Y: OUT std_logic
);
end component;

signal A_TB: std_logic_vector(3 downto 0);
signal S_TB: std_logic_vector(1 downto 0);
signal Y_TB: std_logic;

begin
	U1: MUX4_2L1 port map (A => A_TB, S => S_TB, Y => Y_TB);
process begin
	A_TB <= "0110";
	
	S_TB <= "00";
	wait for 10 ns; --deve ser escolhido o 0 -> 1
	S_TB <= "01";
	wait for 10 ns; --deve ser escolhido o 1 -> 0
	S_TB <= "10";
	wait for 10 ns; --deve ser escolhido o 1 -> 0
	S_TB <= "11";
	wait for 10 ns; --deve ser escolhido o 0 -> 1
	
	A_TB <= "1010";
	
	S_TB <= "00";
	wait for 10 ns; --deve ser escolhido o 0 -> 1
	S_TB <= "01";
	wait for 10 ns; --deve ser escolhido o 1 -> 0
	S_TB <= "10";
	wait for 10 ns; --deve ser escolhido o 0 -> 1
	S_TB <= "11";
	wait for 10 ns; --deve ser escolhido o 1 -> 0
	
	A_TB <= "0101";
	
	S_TB <= "00";
	wait for 10 ns; --deve ser escolhido o 1 -> 0
	S_TB <= "01";
	wait for 10 ns; --deve ser escolhido o 0 -> 1
	S_TB <= "10";
	wait for 10 ns; --deve ser escolhido o 1 -> 0
	S_TB <= "11";
	wait for 10 ns; --deve ser escolhido o 0 -> 1

	
	wait;
end process;
end MUX4_2L1_tb_arch;