package com.ems.lite.admin.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ems.lite.admin.model.Influencer
import com.ems.lite.admin.model.Survey
import com.ems.lite.admin.model.table.CountBy
import com.ems.lite.admin.model.table.Voter


@Dao
abstract class VoterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: List<Voter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: Voter)

    @Query("SELECT * FROM Voter where updated=:isUpdate  ")
    abstract fun getUpdatedVoter(isUpdate: String): List<Voter>?

    @Query("SELECT * FROM Voter where _id=:id")
    abstract fun get(id: Int): Voter?

    @Query("DELETE FROM Voter")
    abstract fun clear(): Int

    //    @Query(
//        "SELECT * FROM (" +
//                "SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName=:surname) AND (voterFNameEng LIKE :s1||'%' OR voterFName LIKE :s1||'%' OR :s1='') and (voterMNameEng LIKE :s2||'%' OR voterMName LIKE :s2||'%' OR  :s2='') and (voterLNameEng LIKE :s3||'%' OR  voterLName LIKE :s3||'%' OR  :s3='') " +
//                "UNION  SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName=:surname) AND (voterLNameEng LIKE :s1||'%' OR voterLName LIKE :s1||'%' OR :s1='') and (voterFNameEng LIKE :s2||'%' OR voterFName LIKE :s2||'%' OR :s2='') and (voterMNameEng LIKE :s3||'%' OR voterMName LIKE :s3||'%' OR :s3='') " +
//                "UNION  SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName=:surname) AND (voterLNameEng LIKE :s1||'%' OR voterLName LIKE :s1||'%' OR :s1='') and (voterFNameEng LIKE :s2||'%' OR voterFName LIKE :s2||'%' OR :s2='')" +
//                "UNION  SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName=:surname) AND (voterFNameEng LIKE :s1||'%' OR  voterFName LIKE :s1||'%' OR :s1='') and (voterLNameEng LIKE :s2||'%' OR voterLName LIKE :s2||'%' OR :s2='')" +
//                ")Voter  ORDER BY voterFNameEng  LIMIT :offset,20"
//    )
    @Query(
        "SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName=:surname) AND (voterFNameEng LIKE :firstName||'%' OR voterFName LIKE :firstName||'%' OR :firstName='') AND (voterMNameEng LIKE :middleName||'%' OR voterMName LIKE :middleName||'%' OR  :middleName='') and (voterLNameEng LIKE :lastName||'%' OR  voterLName LIKE :lastName||'%' OR  :lastName='')   ORDER BY voterFNameEng  LIMIT :offset,20"
    )
    abstract fun searchVoter(
        firstName: String, middleName: String, lastName: String,
        villageNo: Long, boothNo: Long,
        surname: String?, offset: Int
    ): List<Voter>?

    @Query(
        "SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterFNameEng LIKE :firstName||'%' OR voterFName LIKE :firstName||'%' OR :firstName='') AND (voterMNameEng LIKE :middleName||'%' OR voterMName LIKE :middleName||'%' OR  :middleName='') and (voterLNameEng LIKE :lastName||'%' OR  voterLName LIKE :lastName||'%' OR  :lastName='')   ORDER BY voterNo  LIMIT :offset,20"
    )
    abstract fun searchVoter(
        firstName: String, middleName: String, lastName: String,
        villageNo: Long, boothNo: Long,
        offset: Int
    ): List<Voter>?

    @Query(
        "SELECT * FROM (" +
                "SELECT * FROM Voter WHERE (:voterCard =='' OR cardNo == :voterCard) AND (:houseNo =='' OR houseNo == :houseNo) AND (:gender =='' OR sex == :gender) AND ((:fromAge =='' AND :toAge =='') OR (:fromAge !='' AND :toAge !='' AND age BETWEEN :fromAge AND :toAge)) AND (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterFNameEng LIKE :s1||'%' OR :s1='') and (voterMNameEng LIKE :s2||'%' OR :s2='') and (voterLNameEng LIKE :s3||'%' OR :s3='') " +
                "UNION  SELECT * FROM Voter WHERE (:voterCard =='' OR cardNo == :voterCard) AND (:houseNo =='' OR houseNo == :houseNo) AND (:gender =='' OR sex == :gender) AND ((:fromAge =='' AND :toAge =='') OR (:fromAge !='' AND :toAge !='' AND age BETWEEN :fromAge AND :toAge))  AND (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng LIKE :s1||'%' OR :s1='') and (voterFNameEng LIKE :s2||'%' OR :s2='') and (voterMNameEng LIKE :s3||'%' OR :s3='') " +
                "UNION  SELECT * FROM Voter WHERE (:voterCard =='' OR cardNo == :voterCard) AND (:houseNo =='' OR houseNo == :houseNo) AND (:gender =='' OR sex == :gender) AND ((:fromAge =='' AND :toAge =='') OR (:fromAge !='' AND :toAge !='' AND age BETWEEN :fromAge AND :toAge)) AND (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng LIKE :s1||'%' OR :s1='') and (voterFNameEng LIKE :s2||'%' OR :s2='')" +
                "UNION  SELECT * FROM Voter WHERE (:voterCard =='' OR cardNo == :voterCard) AND (:houseNo =='' OR houseNo == :houseNo) AND (:gender =='' OR sex == :gender) AND ((:fromAge =='' AND :toAge =='') OR (:fromAge !='' AND :toAge !='' AND age BETWEEN :fromAge AND :toAge)) AND (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterFNameEng LIKE :s1||'%' OR :s1='') and (voterLNameEng LIKE :s2||'%' OR :s2='')" +
                "UNION  SELECT * FROM Voter WHERE (:voterCard =='' OR cardNo == :voterCard) AND (:houseNo =='' OR houseNo == :houseNo) AND (:gender =='' OR sex == :gender) AND ((:fromAge =='' AND :toAge =='') OR (:fromAge !='' AND :toAge !='' AND age BETWEEN :fromAge AND :toAge)) AND (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterNo LIKE :s1||'%' OR :s1='')" +
                "UNION  SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (address LIKE :s1||'%' OR :s1='')" +
                "UNION  SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (cardNo LIKE :s1||'%' OR :s1='')" +
                ")Voter   LIMIT :offset,30"
    )
    abstract fun searchVoterByAge(
        s1: String, s2: String, s3: String,
        villageNo: Long, boothNo: String,
        voterCard: String,
        houseNo: String,
        gender: String,
        fromAge: String,
        toAge: String,
        offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM (SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterNameEng LIKE '%'||:name||'%' OR voterName LIKE '%'||:name||'%' OR :name=''))Voter ORDER BY voterNo LIMIT :offset,30")
    abstract fun searchVoterByName(
        name: String,
        villageNo: Long, boothNo: Long,
        offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM (SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName =:lastName) AND (voterNo == :voterNo OR :voterNo=''))Voter ORDER BY voterNo LIMIT :offset,20")
    abstract fun searchVoterByVoterNo(
        voterNo: String,
        villageNo: Long,
        boothNo: Long,
        lastName: String?,
        offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM (SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterNo == :voterNo OR :voterNo=''))Voter ORDER BY voterNo LIMIT :offset,20")
    abstract fun searchVoterByVoterNo(
        voterNo: String, villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM (SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName=:lastName) AND (cardNo LIKE :cardNo||'%' OR :cardNo=''))Voter ORDER BY cardNo,voterNo LIMIT :offset,20")
    abstract fun searchVoterByCardNo(
        cardNo: String, villageNo: Long, boothNo: Long,
        lastName: String?, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM (SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (cardNo LIKE :cardNo||'%' OR :cardNo=''))Voter ORDER BY cardNo,voterNo LIMIT :offset,20")
    abstract fun searchVoterByCardNo(
        cardNo: String, villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM (SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLName=:surname) AND (houseNo LIKE :houseNo||'%' OR :houseNo='')) Voter ORDER BY houseNo,voterNo LIMIT :offset,20")
    abstract fun searchVoterByHouseNo(
        houseNo: String, villageNo: Long, boothNo: Long,
        surname: String?, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM (SELECT * FROM Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (houseNo LIKE :houseNo||'%' OR :houseNo='')) Voter ORDER BY houseNo,voterNo LIMIT :offset,20")
    abstract fun searchVoterByHouseNo(
        houseNo: String, villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?


    @Query("SELECT * FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) ORDER BY voterNameEng ASC LIMIT :offset,30")
    abstract fun getAllOrder(
        villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) ORDER BY voterLNameEng ASC")
    abstract fun getAllOrder(villageNo: Long, boothNo: Long): List<Voter>?

    @Query("SELECT * FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) ORDER BY voterNo ASC LIMIT :offset,30")
    abstract fun getAllOrderVno(
        villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) ORDER BY voterNo ASC")
    abstract fun getAllOrderVno(villageNo: Long, boothNo: Long): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND  voterStatusName=:name LIMIT :offset,30")
    abstract fun getVoterByStatus(
        villageNo: Long?, boothNo: Long?, name: String?, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND  voterStatusName=:name")
    abstract fun getVoterByStatus(
        villageNo: Long?, boothNo: Long?,
        name: String?
    ): List<Voter>?

    @Query("SELECT voterLNameEng as nameEng ,voterLName as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (:surname = '' OR voterLNameEng LIKE :surname OR voterLName LIKE :surname) GROUP BY voterLName,voterLNameEng ORDER BY totalCount DESC LIMIT :offset,30")
    abstract fun getCountBYSurname(
        villageNo: Long, boothNo: Long,
        surname: String, offset: Int
    ): List<CountBy>?

    @Query("SELECT voterLNameEng as nameEng ,voterLName as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (:surname = '' OR voterLNameEng LIKE :surname OR voterLName LIKE :surname) GROUP BY voterLName,voterLNameEng ORDER BY totalCount DESC")
    abstract fun getCountBYSurname(
        villageNo: Long, boothNo: Long,
        surname: String
    ): List<CountBy>?

    @Query("SELECT COUNT(*) FROM Voter WHERE completedFamily=:completed GROUP BY houseNo")
    abstract fun getFamilyCount(completed: Int): Long

    @Query("SELECT COUNT(*) FROM Voter WHERE (:villageNo =0 OR villageNo = :villageNo)  AND completedFamily=:completed GROUP BY houseNo")
    abstract fun getFamilyCount(
        villageNo: Long, completed: Int
    ): Long

    //    @Query("SELECT _id as id, voterNameEng as nameEng, voterName as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo)  AND (:surname = '' OR voterLNameEng LIKE :surname  OR voterLName LIKE :surname) AND completedFamily=:completed GROUP BY voterLNameEng COLLATE NOCASE , houseNo ORDER BY totalCount DESC LIMIT :offset,30")
    @Query(
        "SELECT COUNT(*) FROM ( SELECT COUNT(houseNo) AS fcount   FROM Voter  WHERE (:villageNo =0 OR villageNo = :villageNo)  AND (:boothNo =0 OR boothNo = :boothNo)  AND completedFamily=:completed GROUP BY houseNo)"
    )
    abstract fun getFamilyCount(
        villageNo: Long, boothNo: Long, completed: Int
    ): Long

    //    @Query("SELECT _id as id, voterNameEng as nameEng, voterName as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (:surname = '' OR voterLNameEng LIKE :surname  OR voterLName LIKE :surname) AND completedFamily=:completed GROUP BY voterLNameEng COLLATE NOCASE , houseNo ORDER BY totalCount DESC LIMIT :offset,30")
    @Query("SELECT _id as id, voterNameEng as nameEng, voterName as name ,COUNT(*) as totalCount FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo =0 OR boothNo = :boothNo)  AND completedFamily =:completed GROUP BY houseNo ORDER BY totalCount DESC LIMIT :offset,30")
    abstract fun getCountByFamily(
        villageNo: Long, boothNo: Long, completed: Int, offset: Int
    ): List<CountBy>?

    @Query(
        "SELECT _id as id, voterNameEng as nameEng, voterName as name ,house_counts.fcount as totalCount FROM Voter JOIN ( SELECT houseNo, COUNT(houseNo) AS fcount   FROM Voter   WHERE (:villageNo =0 OR villageNo = :villageNo)  AND (:boothNo =0 OR boothNo = :boothNo)  AND completedFamily=:completed GROUP BY houseNo) AS house_counts ON Voter.houseNo = house_counts.houseNo where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo =0 OR boothNo = :boothNo)  AND completedFamily=:completed AND (voterFNameEng COLLATE NOCASE LIKE :firstName||'%' OR voterFName COLLATE NOCASE LIKE :firstName||'%' OR :firstName='') AND (voterMNameEng COLLATE NOCASE LIKE :middleName||'%' OR voterMName COLLATE NOCASE LIKE :middleName||'%' OR :middleName='') AND (voterLNameEng COLLATE NOCASE LIKE :lastName||'%' OR voterLName COLLATE NOCASE LIKE :lastName||'%' OR :lastName='') ORDER BY totalCount DESC LIMIT :offset,30"
    )
    abstract fun getCountByFamily(
        villageNo: Long, boothNo: Long,
        firstName: String, middleName: String, lastName: String, completed: Int, offset: Int
    ): List<CountBy>?


    @Query("SELECT _id as id, voterNameEng as nameEng, voterName as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (:surname = '' OR voterLNameEng LIKE :surname  OR voterLName LIKE :surname) GROUP BY voterLNameEng COLLATE NOCASE , houseNo ORDER BY voterLNameEng")
    abstract fun getCountByFamily(
        villageNo: Long, boothNo: Long?,
        surname: String
    ): List<CountBy>?

    @Query("SELECT * FROM Voter WHERE   (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng=:name OR voterLName=:name) AND castNo!=0 LIMIT :offset,30")
    abstract fun getVoterBySurnameWithCaste(
        villageNo: Long, boothNo: Long,
        name: String, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE   (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng=:name OR voterLName=:name) AND castNo!=0")
    abstract fun getVoterBySurnameWithCaste(
        villageNo: Long, boothNo: Long,
        name: String
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE   (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng=:name OR voterLName=:name) AND castNo==0 LIMIT :offset,30")
    abstract fun getVoterBySurnameWithoutCaste(
        villageNo: Long, boothNo: Long,
        name: String, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE   (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng=:name OR voterLName=:name) AND castNo==0")
    abstract fun getVoterBySurnameWithoutCaste(
        villageNo: Long, boothNo: Long,
        name: String
    ): List<Voter>?

    @Query("SELECT COUNT(*) FROM Voter WHERE   (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng=:name OR voterLName=:name) AND castNo!=0")
    abstract fun getVoterBySurnameWithCasteCount(
        villageNo: Long, boothNo: Long,
        name: String
    ): Long

    @Query("SELECT COUNT(*) FROM Voter WHERE   (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterLNameEng=:name OR voterLName=:name) AND castNo==0")
    abstract fun getVoterBySurnameWithoutCasteCount(
        villageNo: Long, boothNo: Long,
        name: String
    ): Long

    @Query("SELECT address as nameEng, address as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (:address = '' OR address LIKE '%'||:address||'%') GROUP BY address ORDER BY totalCount DESC LIMIT :offset,30")
    abstract fun getCountBYAddress(
        villageNo: Long?, boothNo: Long?,
        address: String?, offset: Int
    ): List<CountBy>?

    @Query("SELECT address as nameEng, address as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (:address = '' OR address LIKE '%'||:address||'%') GROUP BY address ORDER BY totalCount DESC")
    abstract fun getCountBYAddress(
        villageNo: Long?, boothNo: Long?,
        address: String?
    ): List<CountBy>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND  address=:name LIMIT :offset,30")
    abstract fun getVoterByAddress(
        villageNo: Long, boothNo: Long,
        name: String, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND  address=:name")
    abstract fun getVoterByAddress(
        villageNo: Long, boothNo: Long,
        name: String
    ): List<Voter>?

    @Query("SELECT Voter.voterStatusName as nameEng ,Voter.voterStatusName as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) GROUP BY voterStatusName LIMIT :offset,30")
    abstract fun getCountByStatus(
        villageNo: Long?, boothNo: Long?, offset: Int
    ): List<CountBy>?

    @Query("SELECT Voter.voterStatusName as nameEng ,Voter.voterStatusName as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) GROUP BY voterStatusName")
    abstract fun getCountByStatus(
        villageNo: Long?, boothNo: Long?
    ): List<CountBy>?

    @Query("SELECT `Cast`.castNameEng as nameEng ,`Cast`.castName as name ,COUNT(*) as totalCount FROM Voter INNER JOIN `Cast` ON Voter.castNo== `Cast`.castNo where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo)  GROUP BY Voter.castNo LIMIT :offset,30")
    abstract fun getCountByCast(
        villageNo: Long?, boothNo: Long?, offset: Int
    ): List<CountBy>?

    @Query("SELECT `Cast`.castNameEng as nameEng ,`Cast`.castName as name ,COUNT(*) as totalCount FROM Voter INNER JOIN `Cast` ON Voter.castNo== `Cast`.castNo where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo)  GROUP BY Voter.castNo")
    abstract fun getCountByCast(
        villageNo: Long?, boothNo: Long?
    ): List<CountBy>?

    @Query("SELECT  *  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo=:name LIMIT :offset,30")
    abstract fun getVoterByCast(
        villageNo: Long, boothNo: Long,
        name: String, offset: Int
    ): List<Voter>?

    @Query("SELECT  *  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo=:name")
    abstract fun getVoterByCast(
        villageNo: Long, boothNo: Long,
        name: String
    ): List<Voter>?

    @Query("SELECT  *  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo!=0 LIMIT :offset,30")
    abstract fun getVoterByCastApplied(
        villageNo: Long, boothNo: Long,
        offset: Int
    ): List<Voter>?

    @Query("SELECT  *  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo!=0")
    abstract fun getVoterByCastApplied(
        villageNo: Long, boothNo: Long
    ): List<Voter>?

    @Query("SELECT  *  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo==0 LIMIT :offset,30")
    abstract fun getVoterByCastNotApplied(
        villageNo: Long, boothNo: Long,
        offset: Int
    ): List<Voter>?

    @Query("SELECT  *  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo==0")
    abstract fun getVoterByCastNotApplied(
        villageNo: Long, boothNo: Long
    ): List<Voter>?

    @Query("SELECT  COUNT(*)  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo!=0")
    abstract fun getVoterByCastAppliedCount(
        villageNo: Long, boothNo: Long
    ): Long

    @Query("SELECT  COUNT(*)  FROM Voter   WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND castNo==0")
    abstract fun getVoterByCastNotAppliedCount(
        villageNo: Long, boothNo: Long
    ): Long


    @Query("SELECT outstationAddress as nameEng, outstationAddress as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo)  GROUP BY outstationAddress LIMIT :offset,30")
    abstract fun getCountByStation(
        villageNo: Long?, boothNo: Long?, offset: Int
    ): List<CountBy>?

    @Query("SELECT outstationAddress as nameEng, outstationAddress as name ,COUNT(*) as totalCount FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo)  GROUP BY outstationAddress")
    abstract fun getCountByStation(
        villageNo: Long?, boothNo: Long?
    ): List<CountBy>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND outstationAddress=:name LIMIT :offset,30")
    abstract fun getVoterByStation(
        villageNo: Long, boothNo: Long,
        name: String, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND outstationAddress=:name")
    abstract fun getVoterByStation(
        villageNo: Long, boothNo: Long,
        name: String
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND mobileNo  IS NOT NULL  AND mobileNo !='' ORDER BY voterNo LIMIT :offset,30")
    abstract fun getAllMobile(
        villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND mobileNo  IS NOT NULL  AND mobileNo !='' ORDER BY voterNo")
    abstract fun getAllMobile(
        villageNo: Long, boothNo: Long
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND mobileNo  IS NULL  OR mobileNo =='' ORDER BY voterNo LIMIT :offset,30")
    abstract fun getAllMobileEmpty(
        villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND mobileNo  IS NULL  OR mobileNo =='' ORDER BY voterNo")
    abstract fun getAllMobileEmpty(
        villageNo: Long, boothNo: Long
    ): List<Voter>?

    @Query("SELECT COUNT(*) FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND mobileNo  IS NOT NULL  AND mobileNo !=''")
    abstract fun getAllMobileCount(
        villageNo: Long, boothNo: Long
    ): Long

    @Query("SELECT COUNT(*) FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (mobileNo  IS NULL  OR mobileNo =='')")
    abstract fun getAllMobileEmptyCount(
        villageNo: Long, boothNo: Long
    ): Long

    @Query("SELECT * FROM Voter where _id=:id")
    abstract fun getVoterById(id: Int): Voter?

    @Query("SELECT * FROM Voter where cardNo=:id")
    abstract fun getVoterByCardNo(id: String?): Voter?

    @Query("SELECT COUNT(*) FROM Voter")
    abstract fun getAllCount(): Long

    @Query("SELECT COUNT(*) FROM Voter where (:villageNo ==0 OR villageNo == :villageNo)")
    abstract fun getVillageTotalCount(villageNo: Long): Long

    @Query("SELECT COUNT(*) FROM Voter where (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo)")
    abstract fun getBoothTotalCount(
        villageNo: Long, boothNo: Long
    ): Long

    @Query("SELECT COUNT(*) as totalCount FROM Voter where  villageNo=:villageNo AND age >=  :min AND age <=:max")
    abstract fun getCountByAge(villageNo: String, min: Int, max: Int): Int

    @Query("SELECT COUNT(*) as totalCount FROM Voter where  villageNo=:villageNo AND boothNo=:boothNo AND age >=  :min AND age <=:max")
    abstract fun getCountByAgeByBooth(villageNo: String, boothNo: String, min: Int, max: Int): Int

    @Query("SELECT * FROM Voter WHERE villageNo=:villageNo   AND  (:boothNo ==0 OR boothNo == :boothNo) AND age >=  :min AND age <=:max  ")
    abstract fun getVoterByAge(villageNo: Long?, boothNo: Long?, min: Int, max: Int): List<Voter>?

    @Query("SELECT `Profession`.professionNameEng as nameEng ,`Profession`.professionName as name ,COUNT(*) as totalCount FROM Voter INNER JOIN `Profession` ON Voter.professionNo== `Profession`.professionNo where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo)  GROUP BY Voter.professionNo LIMIT :offset,30")
    abstract fun getCountByProfession(
        villageNo: Long?, boothNo: Long?, offset: Int
    ): List<CountBy>?

    @Query("SELECT `Profession`.professionNameEng as nameEng ,`Profession`.professionName as name ,COUNT(*) as totalCount FROM Voter INNER JOIN `Profession` ON Voter.professionNo== `Profession`.professionNo where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo)  GROUP BY Voter.professionNo")
    abstract fun getCountByProfession(
        villageNo: Long?, boothNo: Long?
    ): List<CountBy>?

    @Query(
        "SELECT v1.cardNo  as cardNo,v1.voterNameEng as nameEng, v1.voterName  as name, totalCount, case when totalCount is null then 0 ELSE totalCount end " +
                " FROM (SELECT cardNo,voterName,voterNameEng from Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (:name='' OR LOWER(voterNameEng) LIKE '%'||LOWER(:name)||'%' OR LOWER(voterName) LIKE '%'||LOWER(:name)||'%') AND vip>0)  v1 left JOIN  (SELECT refVoterNo ,COUNT(cardNo) as totalCount  FROM `Voter` GROUP BY refVoterNo) v2 on ( v1.cardNo=v2.refVoterNo) LIMIT :offset,30"
    )
    abstract fun getCountByImpVoter(
        villageNo: Long?,
        boothNo: Long?,
        name: String?,
        offset: Int
    ): List<CountBy>?

    @Query(
        "SELECT v1.cardNo  as cardNo,v1.voterNameEng as nameEng, v1.voterName  as name, totalCount, case when totalCount is null then 0 ELSE totalCount end " +
                " FROM (SELECT cardNo,voterName,voterNameEng from Voter WHERE (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND vip>0)  v1 left JOIN  (SELECT refVoterNo ,COUNT(cardNo) as totalCount  FROM `Voter` GROUP BY refVoterNo) v2 on ( v1.cardNo=v2.refVoterNo)"
    )
    abstract fun getCountByImpVoter(
        villageNo: Long?,
        boothNo: Long?
    ): List<CountBy>?

    @Query(
        "SELECT m.cardNo,m.refVoterNo,m.voterNameEng,m.voterName,m.count AS totalCount,m.acNo,m.divNo,m.villageNo,m.boothNo,m.boothName,m.boothNameEng FROM (SELECT r.cardNo,v.refVoterNo,r.voterNameEng,r.voterName,v.acNo,v.divNo,v.villageNo,v.count,v.boothNo,b.boothName,b.boothNameEng FROM (SELECT  refVoterNo,acNo,divNo,villageNo , count(cardNo) as count,boothNo FROM Voter WHERE refVoterNo!='' GROUP BY refVoterNo,boothNo) v " +
                "LEFT JOIN (SELECT  cardNo ,voterNameEng,voterName,acNo,divNo,villageNo FROM Voter) r ON (v.refVoterNo=r.cardNo) LEFT JOIN (SELECT boothNo,boothNameEng,boothName FROM Booth ) b ON (b.boothNo =v.boothNo)) m WHERE (:name='' OR m.voterNameEng LIKE '%'||:name||'%' OR m.voterName LIKE '%'||:name||'%') OR (:name='' OR m.boothNameEng LIKE '%'||:name||'%' OR m.boothName LIKE '%'||:name||'%') COLLATE NOCASE ORDER BY boothNo LIMIT :offset,30"
    )
    abstract fun getInfluencerList(name: String?, offset: Int): List<Influencer>?

    @Query("UPDATE  Voter  SET  refVoterNo =:cardNo, updated = 1 where _id=:vId")
    abstract fun updateVoterUnderImpVoter(vId: Int, cardNo: String?): Int

    @Query("UPDATE  Voter  SET  houseNo =:houseNo, updated = 1 where _id=:vId")
    abstract fun updateVoterUnderFamily(vId: Int, houseNo: String?): Int

    @Query("UPDATE  Voter  SET  refVoterNo =:cardNo, updated = 1 where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND (voterNo >= :fromVoterNo AND voterNo<=:toVoterNo)")
    abstract fun updateVoterUnderImpVoter(
        villageNo: Long,
        boothNo: Long, fromVoterNo: Int, toVoterNo: Int, cardNo: String?
    ): Int


    @Query("SELECT  *  FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND professionNo=:name LIMIT :offset,30")
    abstract fun getVoterByProfession(
        villageNo: Long,
        boothNo: Long, name: String, offset: Int
    ): List<Voter>?

    @Query("SELECT  *  FROM Voter WHERE  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND professionNo=:name")
    abstract fun getVoterByProfession(
        villageNo: Long,
        boothNo: Long, name: String
    ): List<Voter>?

    @Query("SELECT * FROM Voter WHERE   (houseNo == NULL OR houseNo == :houseNo) ORDER BY age DESC")
    abstract fun getFamilyList(houseNo: String?): List<Voter>?

    @Query("UPDATE  Voter  SET  castNo =:cast, updated = 1 where (voterLNameEng == :surname OR voterLName == :surname) AND  villageNo=:villageNo  AND  (:boothNo ==0 OR boothNo == :boothNo)")
    abstract fun updateCastBySurnameWard(
        surname: String,
        villageNo: Long?,
        boothNo: Long?,
        cast: Long
    ): Int

    @Query("SELECT * FROM Voter where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND dead=1 ORDER BY  voterName LIMIT :offset,30")
    abstract fun getDeadVoterList(
        villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter a JOIN ( SELECT voterName FROM Voter WHERE (:villageNo = 0 OR villageNo = :villageNo) AND (:boothNo = 0 OR boothNo = :boothNo) GROUP BY voterName HAVING COUNT(*) > 1 ) b ON a.voterName = b.voterName WHERE (:villageNo = 0 OR villageNo = :villageNo) AND (:boothNo = 0 OR boothNo = :boothNo) ORDER BY a.voterName LIMIT :offset,30")
//    @Query("SELECT * FROM Voter a JOIN (SELECT * , COUNT(*) FROM Voter  where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) GROUP BY voterName HAVING COUNT(voterName) > 1) b ON  a.voterName = b.voterName ORDER BY  voterName LIMIT :offset,30")
    abstract fun getByDuplication(
        villageNo: Long, boothNo: Long, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter a JOIN (SELECT * , COUNT(*) FROM Voter  where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) GROUP BY voterName HAVING COUNT(voterName) > 1) b ON  a.voterName = b.voterName ORDER BY  voterName")
    abstract fun getByDuplication(
        villageNo: Long, boothNo: Long
    ): List<Voter>?

    @Query("SELECT * FROM Voter  where (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND ((:type == '' AND committeeDesignation !='') OR (:type != '' AND committeeDesignation ==:type))  ORDER BY boothNo, committeeDesignation ")
    abstract fun getCommitteeList(
        villageNo: Long, boothNo: Long, type: String
    ): List<Voter>?

    @Query("SELECT * FROM Voter as v where v.refVoterNo ==:cardNo  ORDER BY v.boothNo")
    abstract fun getVoterListUnderImpVoter(cardNo: String?): List<Voter>?

    @Query("SELECT * FROM Voter as v where (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) AND v.refVoterNo ==:cardNo  ORDER BY v.boothNo")
    abstract fun getVoterListUnderImpVoter(
        villageNo: Long, boothNo: Long, cardNo: String?
    ): List<Voter>?

    @Query("SELECT * FROM Voter a JOIN (SELECT * , COUNT(*) FROM Voter  where (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) GROUP BY voterName HAVING COUNT(voterName) > 1) b ON  a.voterName = b.voterName ORDER BY  voterName LIMIT :offset,30")
    abstract fun getByMobileCaste(
        villageNo: Long?, boothNo: Long?, offset: Int
    ): List<Voter>?

    @Query("SELECT * FROM Voter a JOIN (SELECT * , COUNT(*) FROM Voter  where  (:villageNo ==0 OR villageNo == :villageNo) AND (:boothNo ==0 OR boothNo == :boothNo) GROUP BY voterName HAVING COUNT(voterName) > 1) b ON  a.voterName = b.voterName ORDER BY  voterName")
    abstract fun getByMobileCaste(
        villageNo: Long?, boothNo: Long?
    ): List<Voter>?

    @Query(
        "SELECT Booth.boothNo, Booth.boothName, Booth.boothNameEng, sum(case when Voter.mobileNo<>'' then 1 else 0 end) mobileCount,sum(case when (Voter.castNo<>'' AND Voter.castNo<>'0') then 1 else 0 end) casteCount, votercount as totalcount from Voter left join (select Voter.boothNo,count(Voter.boothNo) AS votercount,Booth.boothName, Booth.boothNameEng from Voter join Booth  on (Voter.boothNo=Booth.boothNo) WHERE (Booth.boothName LIKE :boothName||'%' OR Booth.boothNameEng LIKE :boothName||'%' OR :boothName='') group by Voter.boothNo) Booth on (Voter.boothNo=Booth.boothNo) where  (:boothNo =0 OR Voter.boothNo=:boothNo) group by Voter.boothNo order by Voter.boothNo  LIMIT :offset,30"
    )
    abstract fun getCountByMobileCaste(
        boothNo: Long?,
        boothName: String?,
        offset: Int
    ): List<Survey>?

    @Query(
        "SELECT Booth.boothNo, Booth.boothName, sum(case when Voter.mobileNo<>'' then 1 else 0 end) mobileCount,sum(case when Voter.castNo<>'' then 1 else 0 end) casteCount, votercount as totalcount from Voter left join (select Voter.boothNo,count(Voter.boothNo) AS votercount,Booth.boothName from Voter join Booth  on (Voter.boothNo=Booth.boothNo) WHERE (Booth.boothName LIKE :boothName||'%' OR :boothName='') group by Voter.boothNo) Booth on (Voter.boothNo=Booth.boothNo) where  Voter.villageNo=:villageNo group by Voter.boothNo order by Voter.boothNo"
    )
    abstract fun getCountByMobileCaste(
        villageNo: Long?,
        boothName: String?
    ): List<Survey>?

}