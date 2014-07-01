package com.lm.Metadomain;

/**
 * @Description 用于运输的工具的相关类

 * @author:lm

 * @time:2013-11-7 上午09:34:06

 */
public class Vehicle {
/***************************属性域***********************************************************************/
	/**容量**/
   private int capacity;
   /**当前工作状态**/
   private boolean isIdle;
   /**到达目标单元的时间(后期扩展中可用)**/
   private int arriveDesTime;
   /**设置下一次回到单元上的时间**/
   private int backTime;		
/***************************方法域***********************************************************************/  

   /**
	 * @Description construction of Vehicle
	 * @param size
	 * @exception:
	 */
   public Vehicle(int size)
   {
	this.capacity=size;
	this.isIdle=true; //默认刚开始均为可用
   }
   
   /**
    * @Description 设置下次回到单元的时间
    * @param 要设置的时间
    */
   public void SetBackTime(int t)
   {
	   backTime=t;
   }
   
   /***
    * 设置到达目的地的时间
    * @param t--要设置的时间
    */
   public void SetArriveDesTime(int t)
   {
	   arriveDesTime=t;
   }
   
   /**
    * 小车回到单元时间是否等于currentTime
    * @param currentTime
    * @return
    */
   public boolean IsTimeEqual(int currentTime) {
	// TODO Auto-generated method stub
	if(this.backTime==currentTime) return true;
	else return false;
   }
   
   /**
    * 改变当前小车的状态
    */
   public void changeIdle() {
	// TODO Auto-generated method stub
	this.isIdle=!(this.isIdle);
   }
   
   /**
    * 获取当前小车的状态
    */
   public boolean getIdle() {
	// TODO Auto-generated method stub
	return this.isIdle;
   }
   
   /**
    * 获取小车的容量
    */
   public int getCapacity() {
	  return this.capacity;
   }
}
