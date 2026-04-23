transcript on
if {[file exists rtl_work]} {
	vdel -lib rtl_work -all
}
vlib rtl_work
vmap work rtl_work

vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/RegistryL3.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/ShiftRegisterL6.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/RegistryL4.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/MUX2_1L6.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/KeyTransmitterControl.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/KeyTransmitter.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/FFD.vhd}

