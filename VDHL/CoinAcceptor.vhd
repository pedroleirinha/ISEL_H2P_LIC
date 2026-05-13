LIBRARY IEEE;
use IEEE.STD_LOGIC_1164.all;


entity CoinAcceptor is
	port ( 
		accept, collect, eject: in STD_LOGIC;
		Coins: out STD_LOGIC_VECTOR(2 downto 0);
		Coin: out STD_LOGIC
	);
		
end CoinAcceptor;

ARCHITECTURE Behaviour OF CoinAcceptor IS


begin


	Coins <= "000";
	Coin	<= '0';

							
end Behaviour;