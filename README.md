# STM32-Bootloader
 STM32 bootloader example that can jump to 2 apps.
  
## Hardware
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Top.jpg?raw=true)  
  
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Bottom.jpg?raw=true)  
  
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Switch_Macro_Angle.jpg?raw=true)  
  
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Switch_Macro_Side.jpg?raw=true)  
  
  
## Software  
  
This bootloader example can jump to 2 different applications.  
  
The FLASH memory (64KB) is splitted into multiple partitions.  
The first partition (20KB) is for the bootloader.  
The second partition is for the Application1 (22KB).
The third partion is for the Application2 (22KB).  
  
To calculate offest in KB in binary, visit: https://www.gbmb.org/kb-to-bytes  
Example:  
20KB is 20480 Bytes.  
  
To find out what is your's MCU Page Size, read a Reference Manual: https://www.st.com/resource/en/reference_manual/cd00171190-stm32f101xx-stm32f102xx-stm32f103xx-stm32f105xx-and-stm32f107xx-advanced-arm-based-32-bit-mcus-stmicroelectronics.pdf    
  
  
## Bootloader
  
A bootloader is just an app that can jump to another application, erase the flash, or write a new data to the FLASH memory.  
Applications are located in different sections of the FLASH memory, after the last bootloader sector ends.  
Therefore applications need to have shifted the FLASH memory origin and offset in the vector table.  
  
###### Linker - FLASH.ld
Keep the flash origin, but change the size of the flash memory according to the bootloader size.  
This bootloader has size under 20KB. Applocations can start right from the next sector.  
```C
/* Memories definition */
MEMORY
{
  RAM    (xrw)    : ORIGIN = 0x20000000,   LENGTH = 20K
  FLASH    (rx)    : ORIGIN = 0x8000000,   LENGTH = 20K /*64K*/
}
```
  
###### C Code - Bootloader
  
```C
#define APP1_START (0x08005000)			//Origin + Bootloader size (20kB)
#define APP2_START (0x0800A800)			//Origin + Bootloader size (20kB) + App1 Bank (22kB)
#define FLASH_BANK_SIZE (0X5800)		//22kB
#define FLASH_PAGE_SIZE_USER (0x400)	//1kB

typedef struct
{
    uint32_t		stack_addr;     // Stack Pointer
    application_t*	func_p;        // Program Counter
} JumpStruct;

void jumpToApp(const uint32_t address)
{
	const JumpStruct* vector_p = (JumpStruct*)address;

	deinitEverything();

	/* let's do The Jump! */
    /* Jump, used asm to avoid stack optimization */
    asm("msr msp, %0; bx %1;" : : "r"(vector_p->stack_addr), "r"(vector_p->func_p));
}

void deinitEverything()
{
	//-- reset peripherals to guarantee flawless start of user application
	HAL_GPIO_DeInit(LED_GPIO_Port, LED_Pin);
	HAL_GPIO_DeInit(USB_ENABLE_GPIO_Port, USB_ENABLE_Pin);
	USBD_DeInit(&hUsbDeviceFS);
	  __HAL_RCC_GPIOC_CLK_DISABLE();
	  __HAL_RCC_GPIOD_CLK_DISABLE();
	  __HAL_RCC_GPIOB_CLK_DISABLE();
	  __HAL_RCC_GPIOA_CLK_DISABLE();
	HAL_RCC_DeInit();
	HAL_DeInit();
	SysTick->CTRL = 0;
	SysTick->LOAD = 0;
	SysTick->VAL = 0;
}
```  
  
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/Deinit_direction.png?raw=true)  
  
## Applications
###### Linker - FLASH.ld  
App1 (Application 1) - FLASH.ld  
```C
/* Memories definition */
MEMORY
{
  RAM    (xrw)    : ORIGIN = 0x20000000,   LENGTH = 20K
  FLASH    (rx)    : ORIGIN = 0x8005000,   LENGTH = 22K /*64K*/
}
```  
  
App2 (Application 2) - FLASH.ld  
```C
/* Memories definition */
MEMORY
{
  RAM    (xrw)    : ORIGIN = 0x20000000,   LENGTH = 20K
  FLASH    (rx)    : ORIGIN = 0x800A800,   LENGTH = 22K /*64K*/
}
```  
  
###### C Code - Applications
  
App1 (Application 1) - Code Summary  
```C
  	  /*
  	   * App1
  	   * change the flash size and flash origin in FLASH.ld file like:
  	   * FLASH    (rx)    : ORIGIN = 0x8005000,   LENGTH = 22K //64K
  	   *
  	   * in system_stm32f1xx.c change VECT_TAB_OFFSET to your new value like 0x00005000U
  	   * #define USER_VECT_TAB_ADDRESS //First uncomment this in system_stm32f1xx.c
  	   * #define VECT_TAB_OFFSET         0x00005000U
  	   */
```  

App1 (Application 1) - system_stm32f1xx.c  
```C
/* Note: Following vector table addresses must be defined in line with linker
         configuration. */
/*!< Uncomment the following line if you need to relocate the vector table
     anywhere in Flash or Sram, else the vector table is kept at the automatic
     remap of boot address selected */
 #define USER_VECT_TAB_ADDRESS //Uncommented this

#if defined(USER_VECT_TAB_ADDRESS)
/*!< Uncomment the following line if you need to relocate your vector Table
     in Sram else user remap will be done in Flash. */
/* #define VECT_TAB_SRAM */
#if defined(VECT_TAB_SRAM)
#define VECT_TAB_BASE_ADDRESS   SRAM_BASE       /*!< Vector Table base address field.
                                                     This value must be a multiple of 0x200. */
#define VECT_TAB_OFFSET         0x00000000U     /*!< Vector Table base offset field.
                                                     This value must be a multiple of 0x200. */
#else
#define VECT_TAB_BASE_ADDRESS   FLASH_BASE      /*!< Vector Table base address field.
                                                     This value must be a multiple of 0x200. */
#define VECT_TAB_OFFSET         0x00005000U     /*!< Vector Table base offset field.
                                                     This value must be a multiple of 0x200. */
#endif /* VECT_TAB_SRAM */
#endif /* USER_VECT_TAB_ADDRESS */
```  
  
  
App2 (Application 2) - Code Summary  
```C
	  /*
	   * App2
	   * change the flash size and flash origin in FLASH.ld file like:
	   * FLASH    (rx)    : ORIGIN = 0x800A800,   LENGTH = 22K //64K
	   *
	   * in system_stm32f1xx.c change VECT_TAB_OFFSET to your new value like 0x0000A800U
	   * #define USER_VECT_TAB_ADDRESS //First uncomment this in system_stm32f1xx.c
	   * #define VECT_TAB_OFFSET         0x0000A800U
	   */
```  

App2 (Application 2) - system_stm32f1xx.c  
```C
/* Note: Following vector table addresses must be defined in line with linker
         configuration. */
/*!< Uncomment the following line if you need to relocate the vector table
     anywhere in Flash or Sram, else the vector table is kept at the automatic
     remap of boot address selected */
 #define USER_VECT_TAB_ADDRESS //Uncommented this

#if defined(USER_VECT_TAB_ADDRESS)
/*!< Uncomment the following line if you need to relocate your vector Table
     in Sram else user remap will be done in Flash. */
/* #define VECT_TAB_SRAM */
#if defined(VECT_TAB_SRAM)
#define VECT_TAB_BASE_ADDRESS   SRAM_BASE       /*!< Vector Table base address field.
                                                     This value must be a multiple of 0x200. */
#define VECT_TAB_OFFSET         0x00000000U     /*!< Vector Table base offset field.
                                                     This value must be a multiple of 0x200. */
#else
#define VECT_TAB_BASE_ADDRESS   FLASH_BASE      /*!< Vector Table base address field.
                                                     This value must be a multiple of 0x200. */
#define VECT_TAB_OFFSET         0x0000A800U     /*!< Vector Table base offset field.
                                                     This value must be a multiple of 0x200. */
#endif /* VECT_TAB_SRAM */
#endif /* USER_VECT_TAB_ADDRESS */
```  
  
  
