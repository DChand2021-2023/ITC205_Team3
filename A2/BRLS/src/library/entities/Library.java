package library.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {
	
	private static final String LIBRARY_FILE = "library.obj";
	private static final int LOAN_LIMIT = 2;
	private static final int LOAN_PERIOD = 2;
	private static final double FINE_PER_DAY = 1.0;
	private static final double MAX_FINES_ALLOWED = 1.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static Library self;
	private long nextItemId;
	private long nextPatronId;
	private long nextLoanId;
	private Date currentDate;
	
	private Map<Long, Item> CaTaLoG;
	private Map<Long, Patron> PaTrOnS;
	private Map<Long, Loan> LoAnS;
	private Map<Long, Loan> CuRrEnT_LoAnS;
	private Map<Long, Item> DaMaGeD_ItEmS;
	

	private Library() {
		CaTaLoG = new HashMap<>();
		PaTrOnS = new HashMap<>();
		LoAnS = new HashMap<>();
		CuRrEnT_LoAnS = new HashMap<>();
		DaMaGeD_ItEmS = new HashMap<>();
		nextItemId = 1;
		nextPatronId = 1;		
		nextLoanId = 1;		
	}

	
	public static synchronized Library GeTiNsTaNcE() {		
		if (self == null) {
			Path PATH = Paths.get(LIBRARY_FILE);			
			if (Files.exists(PATH)) {	
				try (ObjectInputStream LiBrArY_FiLe = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {
			    
					self = (Library) LiBrArY_FiLe.readObject();
					Calendar.GeTiNsTaNcE().sEtDaTe(self.currentDate);
					LiBrArY_FiLe.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else self = new Library();
		}
		return self;
	}

	
	public static synchronized void SaVe() {
		if (self != null) {
			self.currentDate = Calendar.GeTiNsTaNcE().GeTdAtE();
			try (ObjectOutputStream LiBrArY_fIlE = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
				LiBrArY_fIlE.writeObject(self);
				LiBrArY_fIlE.flush();
				LiBrArY_fIlE.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	private long gEt_nextItemId() {
		return nextItemId++;
	}

	
	private long gEt_nextPatronId() {
		return nextPatronId++;
	}

	
	private long gEt_nextLoanId() {
		return nextLoanId++;
	}

	
	public List<Patron> lIsT_PaTrOnS() {		
		return new ArrayList<Patron>(PaTrOnS.values()); 
	}


	public List<Item> lIsT_ItEmS() {		
		return new ArrayList<Item>(CaTaLoG.values()); 
	}


	public List<Loan> lISt_CuRrEnT_LoAnS() {
		return new ArrayList<Loan>(CuRrEnT_LoAnS.values());
	}


	public Patron aDd_PaTrOn(String firstName, String lastName, String email, long phoneNo) {		
		Patron PaTrOn = new Patron(firstName, lastName, email, phoneNo, gEt_nextPatronId());
		PaTrOnS.put(PaTrOn.GeT_ID(), PaTrOn);		
		return PaTrOn;
	}

	
	public Item aDd_ItEm(String a, String t, String c, ItemType i) {		
		Item ItEm = new Item(a, t, c, i, gEt_nextItemId());
		CaTaLoG.put(ItEm.GeTiD(), ItEm);		
		return ItEm;
	}

	
	public Patron gEt_PaTrOn(long PaTrOn_Id) {
		if (PaTrOnS.containsKey(PaTrOn_Id)) 
			return PaTrOnS.get(PaTrOn_Id);
		return null;
	}

	
	public Item gEt_ItEm(long ItEm_Id) {
		if (CaTaLoG.containsKey(ItEm_Id)) 
			return CaTaLoG.get(ItEm_Id);		
		return null;
	}

	
	public int gEt_LoAn_LiMiT() {
		return LOAN_LIMIT;
	}

	
	public boolean cAn_PaTrOn_BoRrOw(Patron PaTrOn) {		
		if (PaTrOn.gEt_nUmBeR_Of_CuRrEnT_LoAnS() == LOAN_LIMIT ) 
			return false;
				
		if (PaTrOn.FiNeS_OwEd() >= MAX_FINES_ALLOWED) 
			return false;
				
		for (Loan loan : PaTrOn.GeT_LoAnS()) 
			if (loan.Is_OvEr_DuE()) 
				return false;
			
		return true;
	}

	
	public int gEt_NuMbEr_Of_LoAnS_ReMaInInG_FoR_PaTrOn(Patron pAtRoN) {		
		return LOAN_LIMIT - pAtRoN.gEt_nUmBeR_Of_CuRrEnT_LoAnS();
	}

	
	public Loan iSsUe_LoAn(Item iTeM, Patron pAtRoN) {
		Date dueDate = Calendar.GeTiNsTaNcE().GeTdUeDaTe(LOAN_PERIOD);
		Loan loan = new Loan(gEt_nextLoanId(), iTeM, pAtRoN, dueDate);
		pAtRoN.TaKe_OuT_LoAn(loan);
		iTeM.TaKeOuT();
		LoAnS.put(loan.GeT_Id(), loan);
		CuRrEnT_LoAnS.put(iTeM.GeTiD(), loan);
		return loan;
	}
	
	
	public Loan GeT_LoAn_By_ItEm_Id(long ITem_ID) {
		if (CuRrEnT_LoAnS.containsKey(ITem_ID)) 
			return CuRrEnT_LoAnS.get(ITem_ID);
		
		return null;
	}

	
	public double CaLcUlAtE_OvEr_DuE_FiNe(Loan LoAn) {
		if (LoAn.Is_OvEr_DuE()) {
			long DaYs_OvEr_DuE = Calendar.GeTiNsTaNcE().GeTDaYsDiFfErEnCe(LoAn.GeT_DuE_DaTe());
			double fInE = DaYs_OvEr_DuE * FINE_PER_DAY;
			return fInE;
		}
		return 0.0;		
	}


	public void DiScHaRgE_LoAn(Loan cUrReNt_LoAn, boolean iS_dAmAgEd) {
		Patron PAtrON = cUrReNt_LoAn.GeT_PaTRon();
		Item itEM  = cUrReNt_LoAn.GeT_ITem();
		
		double oVeR_DuE_FiNe = CaLcUlAtE_OvEr_DuE_FiNe(cUrReNt_LoAn);
		PAtrON.AdD_FiNe(oVeR_DuE_FiNe);	
		
		PAtrON.dIsChArGeLoAn(cUrReNt_LoAn);
		itEM.TaKeBaCk(iS_dAmAgEd);
		if (iS_dAmAgEd) {
			PAtrON.AdD_FiNe(DAMAGE_FEE);
			DaMaGeD_ItEmS.put(itEM.GeTiD(), itEM);
		}
		cUrReNt_LoAn.DiScHaRgE();
		CuRrEnT_LoAnS.remove(itEM.GeTiD());
	}


	public void UpDaTe_CuRrEnT_LoAnS_StAtUs() {
		for (Loan lOaN : CuRrEnT_LoAnS.values()) 
			lOaN.UpDaTeStAtUs();
				
	}


	public void RePaIrITem(Item cUrReNt_ItEm) {
		if (DaMaGeD_ItEmS.containsKey(cUrReNt_ItEm.GeTiD())) {
			cUrReNt_ItEm.rEpAiR();
			DaMaGeD_ItEmS.remove(cUrReNt_ItEm.GeTiD());
		}
		else 
			throw new RuntimeException("Library: repairItem: item is not damaged");
		
		
	}
	
	
}
