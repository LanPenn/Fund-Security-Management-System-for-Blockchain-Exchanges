package com.example.graduationdsign.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Account {
    @TableId(type = IdType.AUTO)
   private Integer id;
   private int  bau;
   private double  bae;
   private double  bab;
   private LocalDateTime updatetime ;
  private int userid;

   public Account(int bau, double bae, double bab, LocalDateTime updatetime, int userid) {

      this.bau = bau;
      this.bae = bae;
      this.bab = bab;
      this.updatetime = updatetime;
      this.userid = userid;
   }
   public Account(Integer id, int bau, double bae, double bab, LocalDateTime updatetime, int userid) {
      this.id = id;
      this.bau = bau;
      this.bae = bae;
      this.bab = bab;
      this.updatetime = updatetime;
      this.userid = userid;
   }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", bau=" + bau +
                ", bae=" + bae +
                ", bab=" + bab +
                ", updatetime=" + updatetime +
                ", userid=" + userid +
                '}';
    }
}
