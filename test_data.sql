-- =========================================
-- PULIZIA GENERALE DATABASE BIBLIOTECA
-- =========================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE prestito;
TRUNCATE TABLE materiale;
TRUNCATE TABLE utente;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- UTENTI (20 utenti + 2 admin) - ID randomici
-- =========================================

-- ADMIN
INSERT INTO utente (id_ut, username, email, password, cod_fiscale, nome, cognome, telefono, bloccato, reg_data, ruolo) VALUES
(147, 'admin', 'admin@biblioteca.it', 'admin123', 'ADMNST80A01H501Z', 'Admin', 'Sistema', 3330000001, 0, '2023-01-01', 'ADMIN'),
(283, 'dem', 'dem@biblioteca.it', 'dem123', 'DEMDMN80A01H501Y', 'Dem', 'Admin', 3330000002, 0, '2023-01-01', 'ADMIN');

-- UTENTI NORMALI
INSERT INTO utente (id_ut, username, email, password, cod_fiscale, nome, cognome, telefono, bloccato, reg_data, ruolo) VALUES
(312, 'mrossi', 'm.rossi@mail.it', 'pwd123', 'RSSMRA90A01H501A', 'Mario', 'Rossi', 3331111111, 0, '2023-02-10', 'USER'),
(458, 'lbianchi', 'l.bianchi@mail.it', 'pwd123', 'BNCLCU85B22F205B', 'Luca', 'Bianchi', 3332222222, 0, '2023-02-14', 'USER'),
(523, 'gverdi', 'g.verdi@mail.it', 'pwd123', 'VRDGNN92C11L219C', 'Gianna', 'Verdi', 3333333333, 0, '2023-03-01', 'USER'),
(671, 'fneri', 'f.neri@mail.it', 'pwd123', 'NREFNC88D44M082D', 'Franco', 'Neri', 3334444444, 0, '2023-03-20', 'USER'),
(742, 'sblu', 's.blu@mail.it', 'pwd123', 'BLUSRA95E55R459E', 'Sara', 'Blu', 3335555555, 0, '2023-04-05', 'USER'),
(819, 'agialli', 'a.gialli@mail.it', 'pwd123', 'GLLNDR87F15L736F', 'Andrea', 'Gialli', 3336666666, 0, '2023-04-12', 'USER'),
(934, 'mviola', 'm.viola@mail.it', 'pwd123', 'VLOMRC91G22M345G', 'Marco', 'Viola', 3337777777, 0, '2023-05-01', 'USER'),
(1052, 'cricci', 'c.ricci@mail.it', 'pwd123', 'RCCCRL89H33N456H', 'Carla', 'Ricci', 3338888888, 0, '2023-05-15', 'USER'),
(1187, 'pmarini', 'p.marini@mail.it', 'pwd123', 'MRNPLA93I44O567I', 'Paolo', 'Marini', 3339999999, 0, '2023-06-01', 'USER'),
(1263, 'efontana', 'e.fontana@mail.it', 'pwd123', 'FNTLNA88L55P678L', 'Elena', 'Fontana', 3340000001, 0, '2023-06-20', 'USER'),
(1398, 'gferrari', 'g.ferrari@mail.it', 'pwd123', 'FRRGPP90M66Q789M', 'Giuseppe', 'Ferrari', 3340000002, 0, '2023-07-01', 'USER'),
(1456, 'lromano', 'l.romano@mail.it', 'pwd123', 'RMNLRA92N77R890N', 'Laura', 'Romano', 3340000003, 0, '2023-07-15', 'USER'),
(1534, 'dcolombo', 'd.colombo@mail.it', 'pwd123', 'CLMDVD86O88S901O', 'Davide', 'Colombo', 3340000004, 0, '2023-08-01', 'USER'),
(1687, 'fgreco', 'f.greco@mail.it', 'pwd123', 'GRCFBA94P99T012P', 'Fabio', 'Greco', 3340000005, 0, '2023-08-20', 'USER'),
(1723, 'abruno', 'a.bruno@mail.it', 'pwd123', 'BRNLSS91Q00U123Q', 'Alessia', 'Bruno', 3340000006, 0, '2023-09-01', 'USER'),
(1891, 'mgallo', 'm.gallo@mail.it', 'pwd123', 'GLLMTT89R11V234R', 'Matteo', 'Gallo', 3340000007, 0, '2023-09-15', 'USER'),
(1945, 'sconti', 's.conti@mail.it', 'pwd123', 'CNTSRA87S22W345S', 'Sara', 'Conti', 3340000008, 0, '2023-10-01', 'USER'),
(2067, 'rdemaria', 'r.demaria@mail.it', 'pwd123', 'DMRRCC93T33X456T', 'Riccardo', 'De Maria', 3340000009, 0, '2023-10-20', 'USER'),
(2198, 'vbarbieri', 'v.barbieri@mail.it', 'pwd123', 'BRBVLN90U44Y567U', 'Valentina', 'Barbieri', 3340000010, 1, '2023-11-01', 'USER');

-- =========================================
-- MATERIALE - 50 LIBRI (ID randomici)
-- =========================================

INSERT INTO materiale (id_pz, tipo, titolo, autore, isbn, anno_pub, ed_num, disp, ins_data) VALUES
(2015, 'LIBRO', 'Il nome della rosa', 'Umberto Eco', '9788845249250', 1980, NULL, 1, '2023-01-01'),
(2147, 'LIBRO', '1984', 'George Orwell', '9780451524935', 1949, NULL, 0, '2023-01-02'),
(2283, 'LIBRO', 'Il signore degli anelli', 'J.R.R. Tolkien', '9780261102385', 1954, NULL, 1, '2023-01-03'),
(2356, 'LIBRO', 'Clean Code', 'Robert C. Martin', '9780132350884', 2008, NULL, 1, '2023-01-10'),
(2478, 'LIBRO', 'Design Patterns', 'Gang of Four', '9780201633610', 1994, NULL, 0, '2023-01-12'),
(2534, 'LIBRO', 'Il piccolo principe', 'Antoine de Saint-Exupery', '9788845292613', 1943, NULL, 1, '2023-01-15'),
(2689, 'LIBRO', 'Don Chisciotte', 'Miguel de Cervantes', '9788804668237', 1605, NULL, 1, '2023-01-20'),
(2745, 'LIBRO', 'Divina Commedia', 'Dante Alighieri', '9788804671541', 1321, NULL, 1, '2023-02-01'),
(2867, 'LIBRO', 'I promessi sposi', 'Alessandro Manzoni', '9788804668220', 1827, NULL, 0, '2023-02-05'),
(2923, 'LIBRO', 'Il Gattopardo', 'Giuseppe Tomasi di Lampedusa', '9788807881374', 1958, NULL, 1, '2023-02-10'),
(3056, 'LIBRO', 'Se questo e un uomo', 'Primo Levi', '9788806219352', 1947, NULL, 1, '2023-02-15'),
(3142, 'LIBRO', 'La coscienza di Zeno', 'Italo Svevo', '9788807900136', 1923, NULL, 1, '2023-02-20'),
(3278, 'LIBRO', 'Il fu Mattia Pascal', 'Luigi Pirandello', '9788804668244', 1904, NULL, 0, '2023-03-01'),
(3389, 'LIBRO', 'Uno nessuno centomila', 'Luigi Pirandello', '9788804671558', 1926, NULL, 1, '2023-03-05'),
(3456, 'LIBRO', 'Il barone rampante', 'Italo Calvino', '9788804668251', 1957, NULL, 1, '2023-03-10'),
(3523, 'LIBRO', 'Le citta invisibili', 'Italo Calvino', '9788804671565', 1972, NULL, 1, '2023-03-15'),
(3678, 'LIBRO', 'Harry Potter e la pietra filosofale', 'J.K. Rowling', '9788831003384', 1997, NULL, 0, '2023-03-20'),
(3745, 'LIBRO', 'Il codice da Vinci', 'Dan Brown', '9788804640745', 2003, NULL, 1, '2023-04-01'),
(3867, 'LIBRO', 'Orgoglio e pregiudizio', 'Jane Austen', '9788807900099', 1813, NULL, 1, '2023-04-05'),
(3934, 'LIBRO', 'Cime tempestose', 'Emily Bronte', '9788807900105', 1847, NULL, 1, '2023-04-10'),
(4023, 'LIBRO', 'Delitto e castigo', 'Fedor Dostoevskij', '9788807900112', 1866, NULL, 0, '2023-04-15'),
(4156, 'LIBRO', 'Guerra e pace', 'Lev Tolstoj', '9788807900129', 1869, NULL, 1, '2023-04-20'),
(4289, 'LIBRO', 'Anna Karenina', 'Lev Tolstoj', '9788807900136', 1877, NULL, 1, '2023-05-01'),
(4367, 'LIBRO', 'Cent anni di solitudine', 'Gabriel Garcia Marquez', '9788804668275', 1967, NULL, 1, '2023-05-05'),
(4478, 'LIBRO', 'Il vecchio e il mare', 'Ernest Hemingway', '9788804668282', 1952, NULL, 0, '2023-05-10'),
(4534, 'LIBRO', 'Moby Dick', 'Herman Melville', '9788807900143', 1851, NULL, 1, '2023-05-15'),
(4623, 'LIBRO', 'Frankenstein', 'Mary Shelley', '9788807900150', 1818, NULL, 1, '2023-05-20'),
(4789, 'LIBRO', 'Dracula', 'Bram Stoker', '9788807900167', 1897, NULL, 1, '2023-06-01'),
(4856, 'LIBRO', 'Il ritratto di Dorian Gray', 'Oscar Wilde', '9788807900174', 1890, NULL, 0, '2023-06-05'),
(4923, 'LIBRO', 'Lo straniero', 'Albert Camus', '9788845292620', 1942, NULL, 1, '2023-06-10'),
(5034, 'LIBRO', 'Il processo', 'Franz Kafka', '9788807900181', 1925, NULL, 1, '2023-06-15'),
(5156, 'LIBRO', 'La metamorfosi', 'Franz Kafka', '9788807900198', 1915, NULL, 1, '2023-06-20'),
(5278, 'LIBRO', 'Ulisse', 'James Joyce', '9788807900204', 1922, NULL, 0, '2023-07-01'),
(5389, 'LIBRO', 'Mrs Dalloway', 'Virginia Woolf', '9788807900211', 1925, NULL, 1, '2023-07-05'),
(5467, 'LIBRO', 'Gita al faro', 'Virginia Woolf', '9788807900228', 1927, NULL, 1, '2023-07-10'),
(5534, 'LIBRO', 'Il grande Gatsby', 'F. Scott Fitzgerald', '9788807900235', 1925, NULL, 1, '2023-07-15'),
(5678, 'LIBRO', 'Sulla strada', 'Jack Kerouac', '9788807900242', 1957, NULL, 0, '2023-07-20'),
(5789, 'LIBRO', 'Lolita', 'Vladimir Nabokov', '9788807900259', 1955, NULL, 1, '2023-08-01'),
(5867, 'LIBRO', 'Il maestro e Margherita', 'Michail Bulgakov', '9788807900266', 1967, NULL, 1, '2023-08-05'),
(5923, 'LIBRO', 'Doctor Zivago', 'Boris Pasternak', '9788807900273', 1957, NULL, 1, '2023-08-10'),
(6045, 'LIBRO', 'I fratelli Karamazov', 'Fedor Dostoevskij', '9788807900280', 1880, NULL, 0, '2023-08-15'),
(6156, 'LIBRO', 'Il giardino dei Finzi-Contini', 'Giorgio Bassani', '9788807900297', 1962, NULL, 1, '2023-08-20'),
(6278, 'LIBRO', 'Il deserto dei Tartari', 'Dino Buzzati', '9788807900303', 1940, NULL, 1, '2023-09-01'),
(6389, 'LIBRO', 'Il giorno della civetta', 'Leonardo Sciascia', '9788807900310', 1961, NULL, 1, '2023-09-05'),
(6456, 'LIBRO', 'Conversazione in Sicilia', 'Elio Vittorini', '9788807900327', 1941, NULL, 0, '2023-09-10'),
(6534, 'LIBRO', 'Cristo si e fermato a Eboli', 'Carlo Levi', '9788807900334', 1945, NULL, 1, '2023-09-15'),
(6678, 'LIBRO', 'Il partigiano Johnny', 'Beppe Fenoglio', '9788807900341', 1968, NULL, 1, '2023-09-20'),
(6789, 'LIBRO', 'Ragazzi di vita', 'Pier Paolo Pasolini', '9788807900358', 1955, NULL, 1, '2023-10-01'),
(6867, 'LIBRO', 'Il conformista', 'Alberto Moravia', '9788807900365', 1951, NULL, 0, '2023-10-05'),
(6923, 'LIBRO', 'La noia', 'Alberto Moravia', '9788807900372', 1960, NULL, 1, '2023-10-10');

-- =========================================
-- MATERIALE - 50 RIVISTE (ID randomici)
-- =========================================

INSERT INTO materiale (id_pz, tipo, titolo, autore, isbn, anno_pub, ed_num, disp, ins_data) VALUES
(7012, 'RIVISTA', 'National Geographic', 'National Geographic Society', NULL, 2024, 202, 1, '2024-01-01'),
(7134, 'RIVISTA', 'Focus', 'Mondadori', NULL, 2024, 350, 1, '2024-01-05'),
(7267, 'RIVISTA', 'Wired', 'Conde Nast', NULL, 2024, 120, 0, '2024-01-10'),
(7345, 'RIVISTA', 'Le Scienze', 'GEDI', NULL, 2024, 665, 1, '2024-01-15'),
(7478, 'RIVISTA', 'Internazionale', 'Internazionale', NULL, 2024, 1550, 1, '2024-01-20'),
(7523, 'RIVISTA', 'Limes', 'GEDI', NULL, 2024, 45, 0, '2024-01-25'),
(7689, 'RIVISTA', 'Espresso', 'GEDI', NULL, 2024, 890, 1, '2024-02-01'),
(7734, 'RIVISTA', 'Panorama', 'Mondadori', NULL, 2024, 1200, 1, '2024-02-05'),
(7867, 'RIVISTA', 'Donna Moderna', 'Mondadori', NULL, 2024, 780, 1, '2024-02-10'),
(7923, 'RIVISTA', 'Auto', 'Quattroruote', NULL, 2024, 156, 0, '2024-02-15'),
(8045, 'RIVISTA', 'Nature', 'Springer Nature', NULL, 2024, 7934, 1, '2024-02-20'),
(8156, 'RIVISTA', 'Science', 'AAAS', NULL, 2024, 6478, 1, '2024-02-25'),
(8278, 'RIVISTA', 'The Economist', 'The Economist Group', NULL, 2024, 9234, 0, '2024-03-01'),
(8389, 'RIVISTA', 'Time', 'Time USA', NULL, 2024, 5623, 1, '2024-03-05'),
(8456, 'RIVISTA', 'Forbes', 'Forbes Media', NULL, 2024, 3456, 1, '2024-03-10'),
(8534, 'RIVISTA', 'Fortune', 'Fortune Media', NULL, 2024, 2345, 1, '2024-03-15'),
(8678, 'RIVISTA', 'Harvard Business Review', 'Harvard Business Publishing', NULL, 2024, 1234, 0, '2024-03-20'),
(8789, 'RIVISTA', 'Scientific American', 'Springer Nature', NULL, 2024, 4567, 1, '2024-03-25'),
(8867, 'RIVISTA', 'New Scientist', 'New Scientist Ltd', NULL, 2024, 3890, 1, '2024-04-01'),
(8923, 'RIVISTA', 'Popular Science', 'Bonnier Corporation', NULL, 2024, 2678, 1, '2024-04-05'),
(9045, 'RIVISTA', 'Discover', 'Kalmbach Media', NULL, 2024, 1890, 0, '2024-04-10'),
(9156, 'RIVISTA', 'Smithsonian', 'Smithsonian Institution', NULL, 2024, 567, 1, '2024-04-15'),
(9278, 'RIVISTA', 'Air and Space', 'Smithsonian Institution', NULL, 2024, 345, 1, '2024-04-20'),
(9389, 'RIVISTA', 'Archaeology', 'Archaeological Institute', NULL, 2024, 234, 1, '2024-04-25'),
(9456, 'RIVISTA', 'History Today', 'History Today Ltd', NULL, 2024, 890, 0, '2024-05-01'),
(9534, 'RIVISTA', 'BBC History', 'Immediate Media', NULL, 2024, 678, 1, '2024-05-05'),
(9678, 'RIVISTA', 'Psychology Today', 'Sussex Publishers', NULL, 2024, 456, 1, '2024-05-10'),
(9789, 'RIVISTA', 'Philosophy Now', 'Anja Publications', NULL, 2024, 156, 1, '2024-05-15'),
(9867, 'RIVISTA', 'MIT Technology Review', 'MIT', NULL, 2024, 234, 0, '2024-05-20'),
(9923, 'RIVISTA', 'IEEE Spectrum', 'IEEE', NULL, 2024, 567, 1, '2024-05-25'),
(10045, 'RIVISTA', 'Communications of the ACM', 'ACM', NULL, 2024, 789, 1, '2024-06-01'),
(10156, 'RIVISTA', 'PC Magazine', 'Ziff Davis', NULL, 2024, 890, 1, '2024-06-05'),
(10278, 'RIVISTA', 'Macworld', 'IDG', NULL, 2024, 456, 0, '2024-06-10'),
(10389, 'RIVISTA', 'Linux Journal', 'Linux Journal LLC', NULL, 2024, 234, 1, '2024-06-15'),
(10456, 'RIVISTA', 'Dr Dobbs Journal', 'UBM Tech', NULL, 2024, 567, 1, '2024-06-20'),
(10534, 'RIVISTA', 'Code Magazine', 'EPS Software', NULL, 2024, 123, 1, '2024-06-25'),
(10678, 'RIVISTA', 'MSDN Magazine', 'Microsoft', NULL, 2024, 345, 0, '2024-07-01'),
(10789, 'RIVISTA', 'Java Magazine', 'Oracle', NULL, 2024, 234, 1, '2024-07-05'),
(10867, 'RIVISTA', 'Python Magazine', 'Python Software', NULL, 2024, 156, 1, '2024-07-10'),
(10923, 'RIVISTA', 'Raspberry Pi Magazine', 'Raspberry Pi Press', NULL, 2024, 89, 1, '2024-07-15'),
(11045, 'RIVISTA', 'Make Magazine', 'Make Community', NULL, 2024, 78, 0, '2024-07-20'),
(11156, 'RIVISTA', 'Wired Italia', 'Conde Nast Italia', NULL, 2024, 234, 1, '2024-07-25'),
(11278, 'RIVISTA', 'Corriere della Sera Magazine', 'RCS', NULL, 2024, 45, 1, '2024-08-01'),
(11389, 'RIVISTA', 'La Repubblica delle Idee', 'GEDI', NULL, 2024, 67, 1, '2024-08-05'),
(11456, 'RIVISTA', 'Il Venerdi', 'GEDI', NULL, 2024, 1890, 0, '2024-08-10'),
(11534, 'RIVISTA', 'Sette', 'RCS', NULL, 2024, 2345, 1, '2024-08-15'),
(11678, 'RIVISTA', 'D Repubblica', 'GEDI', NULL, 2024, 567, 1, '2024-08-20'),
(11789, 'RIVISTA', 'Style Magazine', 'RCS', NULL, 2024, 234, 1, '2024-08-25'),
(11867, 'RIVISTA', 'GQ Italia', 'Conde Nast Italia', NULL, 2024, 345, 0, '2024-09-01'),
(11923, 'RIVISTA', 'Vanity Fair Italia', 'Conde Nast Italia', NULL, 2024, 456, 1, '2024-09-05');

-- =========================================
-- PRESTITI (60 prestiti vari) - ID randomici
-- =========================================

-- Prestiti restituiti regolarmente (senza penale)
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(10234, 312, 2015, '2023-06-01', '2023-06-30', '2023-06-25', 0, 0.00),
(10456, 458, 2283, '2023-06-05', '2023-07-05', '2023-07-01', 0, 0.00),
(10589, 523, 2534, '2023-06-10', '2023-07-10', '2023-07-08', 0, 0.00),
(10723, 671, 2689, '2023-06-15', '2023-07-15', '2023-07-10', 0, 0.00),
(10867, 742, 2745, '2023-07-01', '2023-07-31', '2023-07-28', 0, 0.00),
(10945, 819, 2923, '2023-07-05', '2023-08-04', '2023-08-01', 0, 0.00),
(11078, 934, 3056, '2023-07-10', '2023-08-09', '2023-08-05', 0, 0.00),
(11234, 1052, 3142, '2023-07-15', '2023-08-14', '2023-08-10', 0, 0.00),
(11367, 1187, 3389, '2023-08-01', '2023-08-31', '2023-08-25', 0, 0.00),
(11489, 1263, 3456, '2023-08-05', '2023-09-04', '2023-09-01', 0, 0.00);

-- Prestiti restituiti con ritardo (con penale)
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(11623, 312, 2356, '2023-09-01', '2023-09-30', '2023-10-05', 0, 2.50),
(11756, 458, 2534, '2023-09-05', '2023-10-05', '2023-10-12', 0, 3.50),
(11889, 523, 2745, '2023-09-10', '2023-10-10', '2023-10-20', 0, 5.00),
(11967, 671, 2923, '2023-09-15', '2023-10-15', '2023-10-25', 0, 5.00),
(12045, 742, 3142, '2023-10-01', '2023-10-31', '2023-11-10', 0, 5.00),
(12178, 1398, 3523, '2023-10-05', '2023-11-04', '2023-11-15', 0, 5.50),
(12289, 1456, 3745, '2023-10-10', '2023-11-09', '2023-11-20', 0, 5.50),
(12345, 1534, 3867, '2023-10-15', '2023-11-14', '2023-11-25', 0, 5.50),
(12478, 1687, 3934, '2023-11-01', '2023-11-30', '2023-12-08', 0, 4.00),
(12567, 1723, 4156, '2023-11-05', '2023-12-05', '2023-12-15', 0, 5.00);

-- Prestiti rinnovati e restituiti
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(12689, 312, 4289, '2023-11-10', '2024-01-09', '2024-01-05', 1, 0.00),
(12734, 458, 4367, '2023-11-15', '2024-01-14', '2024-01-10', 1, 0.00),
(12856, 523, 4534, '2023-12-01', '2024-01-30', '2024-01-25', 1, 0.00),
(12923, 671, 4623, '2023-12-05', '2024-02-03', '2024-02-01', 1, 0.00),
(13045, 742, 4789, '2023-12-10', '2024-02-08', '2024-02-05', 1, 0.00),
(13156, 819, 4923, '2023-12-15', '2024-02-13', '2024-02-10', 1, 0.00),
(13278, 934, 7012, '2024-01-01', '2024-01-15', '2024-01-12', 1, 0.00),
(13389, 1052, 7134, '2024-01-05', '2024-01-19', '2024-01-18', 1, 0.00);

-- Prestiti attualmente attivi (non restituiti, non in ritardo)
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(13456, 312, 2015, '2026-01-15', '2026-02-14', NULL, 0, 0.00),
(13523, 458, 2283, '2026-01-18', '2026-02-17', NULL, 0, 0.00),
(13678, 523, 2356, '2026-01-20', '2026-02-19', NULL, 0, 0.00),
(13789, 671, 2534, '2026-01-22', '2026-02-21', NULL, 0, 0.00),
(13867, 742, 2689, '2026-01-25', '2026-02-24', NULL, 0, 0.00),
(13945, 819, 2745, '2026-01-28', '2026-02-27', NULL, 0, 0.00),
(14023, 934, 2923, '2026-01-30', '2026-02-28', NULL, 0, 0.00),
(14156, 1052, 3056, '2026-02-01', '2026-03-03', NULL, 0, 0.00),
(14278, 1187, 3142, '2026-02-02', '2026-03-04', NULL, 0, 0.00),
(14367, 1263, 3389, '2026-02-03', '2026-03-05', NULL, 0, 0.00);

-- Prestiti attivi rinnovati
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(14456, 1398, 3456, '2026-01-01', '2026-02-28', NULL, 1, 0.00),
(14523, 1456, 3523, '2026-01-05', '2026-03-05', NULL, 1, 0.00),
(14678, 1534, 3745, '2026-01-08', '2026-03-08', NULL, 1, 0.00),
(14789, 1687, 3867, '2026-01-10', '2026-03-10', NULL, 1, 0.00),
(14867, 1723, 3934, '2026-01-12', '2026-03-12', NULL, 1, 0.00);

-- Prestiti in ritardo (scaduti, non restituiti)
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(14923, 1891, 2147, '2025-12-01', '2025-12-31', NULL, 0, 0.00),
(15045, 1945, 2478, '2025-12-05', '2026-01-04', NULL, 0, 0.00),
(15156, 2067, 2867, '2025-12-10', '2026-01-09', NULL, 0, 0.00),
(15278, 2198, 3278, '2025-12-15', '2026-01-14', NULL, 0, 0.00),
(15367, 312, 3678, '2025-12-20', '2026-01-19', NULL, 0, 0.00),
(15456, 458, 4023, '2025-12-22', '2026-01-21', NULL, 0, 0.00),
(15523, 523, 4478, '2025-12-25', '2026-01-24', NULL, 0, 0.00),
(15678, 671, 4856, '2025-12-28', '2026-01-27', NULL, 0, 0.00);

-- Prestiti riviste attivi
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(15789, 742, 7012, '2026-02-01', '2026-02-15', NULL, 0, 0.00),
(15867, 819, 7134, '2026-02-02', '2026-02-16', NULL, 0, 0.00),
(15923, 934, 7345, '2026-02-03', '2026-02-17', NULL, 0, 0.00),
(16045, 1052, 7478, '2026-02-04', '2026-02-18', NULL, 0, 0.00);

-- Prestiti riviste in ritardo
INSERT INTO prestito (prest_id, ut_id, mat_id, prest_data, scad_data, rest_data, rinnovato, penale) VALUES
(16156, 1187, 7267, '2026-01-10', '2026-01-24', NULL, 0, 0.00),
(16278, 1263, 7523, '2026-01-12', '2026-01-26', NULL, 0, 0.00),
(16367, 1398, 7923, '2026-01-15', '2026-01-29', NULL, 0, 0.00);

-- =========================================
-- FINE SCRIPT
-- =========================================
